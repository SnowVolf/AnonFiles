package ru.svolf.anonfiles.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.svolf.anonfiles.data.entity.DownloadsItem

/*
 * Created by SVolf on 17.02.2023, 17:51
 * This file is a part of "AnonFiles" project
 */
@Dao
interface HistoryDao {
	@Query("SELECT * FROM history ORDER BY id DESC")
	fun getAll(): Flow<List<DownloadsItem>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun add(item: DownloadsItem)

	@Update(onConflict = OnConflictStrategy.REPLACE)
	suspend fun update(item: DownloadsItem)

	@Delete
	suspend fun remove(item: DownloadsItem)
}