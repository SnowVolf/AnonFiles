package ru.svolf.anonfiles.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.svolf.bullet.Item

/*
 * Created by SVolf on 17.02.2023, 17:55
 * This file is a part of "AnonFiles" project
 */
@Entity(tableName = "History")
data class DownloadsItem(
	// В том числе и TIMESTAMP из которого формируется дата загрузки
	@PrimaryKey @ColumnInfo("id") val id: Long,
	@ColumnInfo("file_name") val fileName: String,
	@ColumnInfo("size_readable") val sizeReadable: String,
	@ColumnInfo("download_link") val link: String,
	// Upload / Download flag. true если файл был загружен юзером
	@ColumnInfo("uploaded") val isUploaded: Boolean = false
	): Item, java.io.Serializable