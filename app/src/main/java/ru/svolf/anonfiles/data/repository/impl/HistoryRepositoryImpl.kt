package ru.svolf.anonfiles.data.repository.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.svolf.anonfiles.data.HistoryDb
import ru.svolf.anonfiles.data.entity.DownloadsItem
import ru.svolf.anonfiles.data.repository.HistoryRepository
import ru.svolf.anonfiles.util.UrlExtractor
import javax.inject.Inject
import javax.inject.Singleton


/*
 * Created by SVolf on 17.02.2023, 21:14
 * This file is a part of "AnonFiles" project
 */
@Singleton
class HistoryRepositoryImpl @Inject constructor(private val database: HistoryDb): HistoryRepository {

	override suspend fun saveToHistory(link: String, sizeReadable: String, uploaded: Boolean) {
		return withContext(Dispatchers.IO){
			database.historyDao().add(DownloadsItem(id = System.currentTimeMillis(), fileName = UrlExtractor.extractName(link),
				sizeReadable = sizeReadable, link = link, isUploaded = uploaded))
		}
	}

	override fun getAllItems() = database.historyDao().getAll()

	override fun disconnect(){
		if (database.isOpen and !database.inTransaction()){
			database.close()
		}
	}
}