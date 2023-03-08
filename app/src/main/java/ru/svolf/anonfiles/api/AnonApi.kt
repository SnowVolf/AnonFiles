package ru.svolf.anonfiles.api

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

/*
 * Created by SVolf on 03.02.2023, 09:45
 * This file is a part of "AnonFiles" project
 */
interface AnonApi {
    @POST("upload")
    @Multipart
    suspend fun upload(@Part file: MultipartBody.Part): AnonResponse

    @GET("v2/file/{id}/info")
    @Headers("Content-Type: application/json", "Accept: application/json", "Connection: keep-alive")
    fun getInfo(@Path(value = "id") fileId: String): Call<AnonResponse>

    @GET
    suspend fun downloadHtml(@Url fileUrl: String): Response<ResponseBody>

    @GET
    @Streaming
    suspend fun downloadFile(@Url fileUrl: String): Response<ResponseBody>
}