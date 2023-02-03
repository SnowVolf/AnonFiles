package ru.svolf.anonfiles.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/*
 * Created by SVolf on 03.02.2023, 09:45
 * This file is a part of "AnonFiles" project
 */
interface AnonApi {
    @POST("upload")
    fun upload(): Call<AnonResponse>

    @GET("v2/file/{id}/info")
    fun getInfo(@Path("id") fileId: String): Call<AnonResponse>
}