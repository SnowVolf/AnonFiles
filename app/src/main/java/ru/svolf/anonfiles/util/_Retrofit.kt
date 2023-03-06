package ru.svolf.anonfiles.util

import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.svolf.anonfiles.api.AnonResponse
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.serialization.decodeFromString

/*
 * Created by SVolf on 02.03.2023, 12:10
 * This file is a part of "AnonFiles" project
 */
suspend fun <T> Call<T>.await(): T {
	return suspendCoroutine { continuation ->
		enqueue(object : Callback<T> {
			override fun onFailure(call: Call<T>, t: Throwable) {
				continuation.resumeWithException(t)
			}

			override fun onResponse(call: Call<T>, response: Response<T>) {
				if (response.isSuccessful) {
					continuation.resume(response.body()!!)
				} else {
					val decodedError = Json.decodeFromString<AnonResponse>(response.errorBody()!!.charStream().readText())
					continuation.resume(decodedError as T)
				}
			}
		})
	}
}