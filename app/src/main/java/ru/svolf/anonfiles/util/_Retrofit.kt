package ru.svolf.anonfiles.util

import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/*
 * Created by SVolf on 02.03.2023, 12:10
 * This file is a part of "AnonFiles" project
 */
suspend fun <T> Call<T>.await(clazz: Class<T>): T {
	return suspendCoroutine { continuation ->
		enqueue(object : Callback<T> {
			override fun onFailure(call: Call<T>, t: Throwable) {
				continuation.resumeWithException(t)
			}

			override fun onResponse(call: Call<T>, response: Response<T>) {
				if (response.isSuccessful) {
					continuation.resume(response.body()!!)
				} else {
					val decodedError = Gson().fromJson(response.errorBody()!!.charStream(), clazz)
					continuation.resume(decodedError)
				}
			}
		})
	}
}