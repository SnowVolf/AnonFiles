package ru.svolf.anonfiles.api

import com.google.gson.annotations.SerializedName

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

data class ApiError(
	@SerializedName("message") val message: String,
	@SerializedName("type") val type: String,
	@SerializedName("code") val code: Int
	) : java.io.Serializable
