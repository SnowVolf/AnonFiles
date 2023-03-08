package ru.svolf.anonfiles.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ru.svolf.anonfiles.data.repository.HistoryRepository
import ru.svolf.anonfiles.data.repository.NetworkRepository
import ru.svolf.anonfiles.data.repository.UploadRepository
import ru.svolf.anonfiles.data.repository.impl.HistoryRepositoryImpl
import ru.svolf.anonfiles.data.repository.impl.NetworkRepositoryImpl
import ru.svolf.anonfiles.data.repository.impl.UploadRepositoryImpl

/*
 * Created by SVolf on 06.03.2023, 20:03
 * This file is a part of "AnonFiles" project
 */
@Module
@InstallIn(ViewModelComponent::class)
interface RepositoriesModule {

	@Binds
	fun provideUploadRepositoryImpl(repository: UploadRepositoryImpl): UploadRepository

	@Binds
	fun provideHistoryRepository(repository: HistoryRepositoryImpl): HistoryRepository

	@Binds
	fun provideNetworkRepository(repository: NetworkRepositoryImpl): NetworkRepository

}