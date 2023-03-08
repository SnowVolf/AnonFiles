package ru.svolf.anonfiles.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.svolf.anonfiles.api.AnonApi
import ru.svolf.anonfiles.util.NetworkStatusTracker
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.ProxySelector
import javax.inject.Singleton


/*
 * Created by SVolf on 13.02.2023, 18:55
 * This file is a part of "AnonFiles" project
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

	@Provides
	@Singleton
	fun provideHttpClient(sharedPreferences: SharedPreferences): OkHttpClient {
		val builder = OkHttpClient.Builder()

		val logger = HttpLoggingInterceptor()
		logger.level = HttpLoggingInterceptor.Level.BODY
		builder.addInterceptor(logger)

		// Check if proxy is enabled and update builder accordingly
		if (sharedPreferences.getBoolean("enable_proxy", false)) {
			val proxy = Proxy(
				Proxy.Type.SOCKS,
				InetSocketAddress(
					sharedPreferences.getString("proxy_server", ""),
					sharedPreferences.getString("proxy_port", "0")!!.toInt()
				)
			)
			builder.proxy(proxy)
		} else {
			builder.proxy(null)
		}

		// Clear any previously set proxy selector to ensure that the new proxy settings are used
		builder.proxySelector(ProxySelector.getDefault())

		return builder.build()
	}

	@Provides
	fun provideSettings(@ApplicationContext context: Context): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)


	@Provides
	fun provideRetrofit(client: OkHttpClient): Retrofit {
		val type: MediaType = "application/json".toMediaTypeOrNull()!!
		val builder = Retrofit.Builder()
		builder.baseUrl("http://api.anonfiles.com/")
		builder.client(client)
		builder.addConverterFactory(GsonConverterFactory.create())
		return builder.build()
	}

	@Provides
	fun provideApi(retrofit: Retrofit): AnonApi = retrofit.create(AnonApi::class.java)

	@Provides
	@Singleton
	fun provideWorker(@ApplicationContext context: Context) = WorkManager.getInstance(context)

	@Provides
	@Singleton
	fun provideNetworkObserver(@ApplicationContext context: Context) = NetworkStatusTracker(context)

}