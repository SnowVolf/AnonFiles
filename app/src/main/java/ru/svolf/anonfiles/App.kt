package ru.svolf.anonfiles

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/*
 * Created by SVolf on 13.02.2023, 12:17
 * This file is a part of "AnonFiles" project
 */
@HiltAndroidApp
class App : Application() {

	override fun onCreate() {
		super.onCreate()
		Timber.plant(Timber.DebugTree())
	}
}