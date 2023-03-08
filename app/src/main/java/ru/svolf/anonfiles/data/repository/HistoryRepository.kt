package ru.svolf.anonfiles.data.repository

import kotlinx.coroutines.flow.Flow
import ru.svolf.anonfiles.data.entity.DownloadsItem


/*
 * Created by SVolf on 06.03.2023, 20:39
 * This file is a part of "AnonFiles" project
 */
interface HistoryRepository {

	suspend fun saveToHistory(link: String, sizeReadable: String, uploaded: Boolean)

	fun getAllItems(): Flow<List<DownloadsItem>>

	fun disconnect()
}