package ru.svolf.anonfiles.data

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.*
import ru.svolf.anonfiles.api.AnonApi
import ru.svolf.anonfiles.api.AnonResponse
import timber.log.Timber
import javax.inject.Inject

class Repository @Inject constructor(val api: AnonApi) {

    fun getData(url: String): AnonResponse? {
        if (url.isBlank()) {
            return null
        }
        var responseData: AnonResponse? = null
        CoroutineScope(Dispatchers.IO).launch {
            try {
                api.getInfo(url).enqueue(object : Callback<AnonResponse> {
                    override fun onResponse(call: Call<AnonResponse>, response: Response<AnonResponse>) {
                        if (response.isSuccessful) {
                            responseData = response.body()
                        } else {
                            Timber.e("onResponse: Response not successful")
                        }
                    }

                    override fun onFailure(call: Call<AnonResponse>, t: Throwable) {
                        Timber.e("onFailure: ", t)
                    }
                })
            } catch (e: TimeoutCancellationException) {
                Timber.e("getData: TimeoutException: ", e)
            } catch (e: Exception) {
                Timber.e("getData: Exception: ", e)
            }
        }
        return responseData
    }

    /**
     * Transforms url into file ID
     * For example: https://anonfiles.com/Eeg9s3scnf/Download_zip -> Eeg9s3scnf
     * @return file id or original link if cannot extract
     */
    private fun extractId(linkId: String): String {
        val regex = "^https?://anonfiles\\.\\w+/(\\w+).+$".toRegex()
        return regex.find(linkId)?.groups?.get(1)?.value ?: linkId
    }
}