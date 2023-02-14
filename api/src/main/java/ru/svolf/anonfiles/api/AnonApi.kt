package ru.svolf.anonfiles.api

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

/*
 * Created by SVolf on 03.02.2023, 09:45
 * This file is a part of "AnonFiles" project
 */
interface AnonApi {
    @POST("upload")
    suspend fun upload(@Part file: MultipartBody.Part): Call<AnonResponse>

    @GET("v2/file/{id}/info")
    suspend fun getInfo(@Path("id") fileId: String): Call<AnonResponse>
}