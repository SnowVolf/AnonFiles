package ru.svolf.anonfiles.di


import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.svolf.anonfiles.data.HistoryDb
import ru.svolf.anonfiles.data.repository.HistoryRepository
import javax.inject.Singleton

/*
 * Created by SVolf on 17.02.2023, 18:33
 * This file is a part of "AnonFiles" project
 */
@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
	@Provides
	@Singleton
	fun provideDbHelper(@ApplicationContext context: Context): HistoryDb {
		return Room.databaseBuilder(context, HistoryDb::class.java, "History_Db").build()
	}

	@Provides
	@Singleton
	fun provideHistoryRepository(db: HistoryDb): HistoryRepository = HistoryRepository(db)
}