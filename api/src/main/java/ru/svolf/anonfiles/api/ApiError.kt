package ru.svolf.anonfiles.api

import android.os.Parcel
import android.os.Parcelable
import kotlinx.serialization.SerialName

/*
 * Created by SVolf on 02.02.2023, 21:30
 * This file is a part of "AnonFiles" project
 */
/**
 * Represents error message
 * @param message readable message which displayed to user
 * @param type type of this error
 * @param code int code of this error
 * @see ErrorCodes
 */
@kotlinx.serialization.Serializable
data class ApiError(@SerialName("message") val message: String, @SerialName("type") val type: String, @SerialName("code") val code: Int) :
	Parcelable {
	constructor(parcel: Parcel) : this(
		parcel.readString()!!,
		parcel.readString()!!,
		parcel.readInt()
	) {
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(message)
		parcel.writeString(type)
		parcel.writeInt(code)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<ApiError> {
		override fun createFromParcel(parcel: Parcel): ApiError {
			return ApiError(parcel)
		}

		override fun newArray(size: Int): Array<ApiError?> {
			return arrayOfNulls(size)
		}
	}
}
