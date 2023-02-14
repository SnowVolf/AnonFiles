package ru.svolf.anonfiles.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import ru.svolf.anonfiles.api.AnonApi
import java.net.InetSocketAddress
import java.net.Proxy

import javax.inject.Singleton

/*
 * Created by SVolf on 13.02.2023, 18:55
 * This file is a part of "AnonFiles" project
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

	@Provides
	@Singleton
	fun provideHttpClient(sharedPreferences: SharedPreferences): OkHttpClient {
		val host = OkHttpClient.Builder()
		if (sharedPreferences.getBoolean("enable_proxy", false)) {
			val proxy = Proxy(
				Proxy.Type.HTTP,
				InetSocketAddress(
					sharedPreferences.getString("proxy_server", ""),
					sharedPreferences.getInt("proxy_port", 0)
				)
			)
			host.proxy(proxy)
		}
		return host.build()
	}

	@Provides
	@Singleton
	fun provideSettings(@ApplicationContext context: Context): SharedPreferences {
		return PreferenceManager.getDefaultSharedPreferences(context)
	}

	@Provides
	@Singleton
	fun provideRetrofit(client: OkHttpClient): Retrofit {
		val type: MediaType = MediaType.parse("application/json")!!
		val builder = Retrofit.Builder()
		builder.baseUrl("https://api.anonfiles.com/")
		builder.addConverterFactory(Json.asConverterFactory(type))
		builder.client(client)
		return builder.build()
	}

	@Provides
	@Singleton
	fun provideApi(retrofit: Retrofit): AnonApi = retrofit.create(AnonApi::class.java)
}