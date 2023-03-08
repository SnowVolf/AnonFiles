package ru.svolf.anonfiles.data.repository.impl

import kotlinx.coroutines.CancellationException
import ru.svolf.anonfiles.api.*
import ru.svolf.anonfiles.data.repository.NetworkRepository
import ru.svolf.anonfiles.util.UrlExtractor
import ru.svolf.anonfiles.util.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkRepositoryImpl @Inject constructor(val api: AnonApi): NetworkRepository {

    private suspend fun getInfoForFile(url: String) = api.getInfo(UrlExtractor.extractId(url)).await(AnonResponse::class.java)

    override suspend fun safeApiCall(url: String): AnonResult {
        return try {
            val call = getInfoForFile(url)
            when(call.status) {
                true -> AnonResult.Success(call.data!!)
                false -> AnonResult.Error(call.error!!)
            }
        } catch (ex: Throwable) {
            if (ex is CancellationException) throw ex
            ex.printStackTrace()
            AnonResult.Error(ApiError("Unknown error", "UNKNOWN", ErrorCodes.ERROR_FILE_INVALID))
        }
    }

}
