package ru.svolf.anonfiles.data.repository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.svolf.anonfiles.api.*
import ru.svolf.anonfiles.util.UrlExtractor
import ru.svolf.anonfiles.util.await
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class NetworkRepository @Inject constructor(val api: AnonApi) {

    private suspend fun getInfoForFile(url: String) = api.getInfo(UrlExtractor.extractId(url)).await()

    suspend fun safeApiCall(url: String): AnonResult {
        return try {
            val call = getInfoForFile(url)
            when(call.status) {
                true -> AnonResult.Success(call.data!!)
                false -> AnonResult.Error(call.error!!)
            }
        } catch (ex: Throwable) {
            if (ex is CancellationException) throw ex
            AnonResult.Error(ApiError("Unknown error", "UNKNOWN", ErrorCodes.ERROR_FILE_INVALID))
        }
    }
}
