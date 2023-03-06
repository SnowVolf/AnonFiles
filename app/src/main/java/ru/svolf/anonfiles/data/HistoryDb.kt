package ru.svolf.anonfiles.data

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.svolf.anonfiles.data.dao.HistoryDao
import ru.svolf.anonfiles.data.entity.DownloadsItem

/*
 * Created by SVolf on 17.02.2023, 18:24
 * This file is a part of "AnonFiles" project
 */
@Database(entities = [DownloadsItem::class], version = 1)
abstract class HistoryDb : RoomDatabase() {
	abstract fun historyDao(): HistoryDao
}