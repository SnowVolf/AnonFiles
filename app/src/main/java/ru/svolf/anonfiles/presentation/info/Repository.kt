package ru.svolf.anonfiles.presentation.info

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.*
import ru.svolf.anonfiles.api.AnonResponse
import ru.svolf.anonfiles.api.ApiClient

object Repository {
    private val TAG = Repository::class.java.simpleName
    private val apiClient: ApiClient = ApiClient(null, 80)
    private val responseData = MutableLiveData<AnonResponse>()

    fun getData(linkId: String): LiveData<AnonResponse> {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                    apiClient.getApi().getInfo(linkId).enqueue(object : Callback<AnonResponse> {
                        override fun onResponse(call: Call<AnonResponse>, response: Response<AnonResponse>) {
                            if (response.isSuccessful) {
                                responseData.postValue(response.body())
                            } else {
                                Log.e(TAG, "onResponse: Response not successful")
                            }
                        }

                        override fun onFailure(call: Call<AnonResponse>, t: Throwable) {
                            Log.e(TAG, "onFailure: ${t.message}")
                        }
                    })
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "getData: TimeoutException: ${e.message}")
            } catch (e: Exception) {
                Log.e(TAG, "getData: Exception: ${e.message}")
            }
        }
        return responseData
    }
}