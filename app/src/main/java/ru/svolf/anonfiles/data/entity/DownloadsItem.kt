package ru.svolf.anonfiles.data.entity

import android.os.Parcel
import android.os.Parcelable
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
	): Item, Parcelable {
	constructor(parcel: Parcel) : this(
		parcel.readLong(),
		parcel.readString()!!,
		parcel.readString()!!,
		parcel.readString()!!,
		parcel.readByte() != 0.toByte()
	) {
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeLong(id)
		parcel.writeString(fileName)
		parcel.writeString(sizeReadable)
		parcel.writeString(link)
		parcel.writeByte(if (isUploaded) 1 else 0)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<DownloadsItem> {
		override fun createFromParcel(parcel: Parcel): DownloadsItem {
			return DownloadsItem(parcel)
		}

		override fun newArray(size: Int): Array<DownloadsItem?> {
			return arrayOfNulls(size)
		}
	}
}
