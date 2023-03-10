package ru.svolf.anonfiles.data.repository.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.await
import ru.svolf.anonfiles.api.*
import ru.svolf.anonfiles.data.repository.NetworkRepository
import ru.svolf.anonfiles.util.UrlExtractor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkRepositoryImpl @Inject constructor(val api: AnonApi): NetworkRepository {

    private suspend fun getInfoForFile(url: String) = api.getInfo(UrlExtractor.extractId(url)).await()

    override suspend fun safeApiCall(url: String): Flow<AnonResult> = flow {
        val call = getInfoForFile(url)
        when(call.status) {
            true -> emit(AnonResult.Success(call.data!!, ModeStrategy.DOWNLOAD))
            false -> emit(AnonResult.Error(call.error!!, ModeStrategy.DOWNLOAD))
        }
    }.catch { ex ->
        ex.printStackTrace()
        emit(AnonResult.Error(ApiError(ex.localizedMessage!!, ex::javaClass.name, ErrorCodes.ERROR_FILE_INVALID), ModeStrategy.DOWNLOAD))
    }

}
