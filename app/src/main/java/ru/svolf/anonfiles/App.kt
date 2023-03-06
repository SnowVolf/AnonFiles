package ru.svolf.anonfiles

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import ru.svolf.anonfiles.service.NotificationConstants
import timber.log.Timber
import javax.inject.Inject

/*
 * Created by SVolf on 13.02.2023, 12:17
 * This file is a part of "AnonFiles" project
 */
@HiltAndroidApp
class App : Application(), Configuration.Provider {
	@Inject lateinit var hiltWorker: HiltWorkerFactory


	override fun onCreate() {
		super.onCreate()
		Timber.plant(Timber.DebugTree())
		AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
		createNotificationChannel()
	}

	override fun getWorkManagerConfiguration(): Configuration {
		return Configuration.Builder().setWorkerFactory(hiltWorker).build()
	}

	private fun createNotificationChannel(){
		val name = getString(R.string.notifications_channel_title)
		val description = getString(R.string.notification_channel_desc)
		val importance = NotificationManager.IMPORTANCE_DEFAULT
		val channel = NotificationChannel(NotificationConstants.CHANNEL_ID, name, importance)
		channel.description = description

		val notificationManager = getSystemService(NotificationManager::class.java)
		notificationManager.createNotificationChannel(channel)
	}

}