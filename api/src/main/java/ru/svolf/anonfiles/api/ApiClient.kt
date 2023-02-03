package ru.svolf.anonfiles.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.net.InetSocketAddress
import java.net.Proxy


/*
 * Created by SVolf on 03.02.2023, 12:45
 * This file is a part of "AnonFiles" project
 */
class ApiClient(proxyHost: String?, proxyPort: Int = 0) {
    private var retrofit: Retrofit
    private var builder: Retrofit.Builder
    private var api: AnonApi
    init {
        val type: MediaType = MediaType.parse("application/json")!!
        builder = Retrofit.Builder()
        builder.baseUrl("https://api.anonfiles.com/")
        builder.addConverterFactory(Json.asConverterFactory(type))
        //146.59.152.52:59166
        if (proxyHost != null) {
            val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress(proxyHost, proxyPort))
            val client = OkHttpClient.Builder().proxy(proxy).retryOnConnectionFailure(true).build()
            builder.client(client)
        }
        retrofit = builder.build()
        api = retrofit.create(AnonApi::class.java)
    }

    fun getApi(): AnonApi {
        return api
    }

}