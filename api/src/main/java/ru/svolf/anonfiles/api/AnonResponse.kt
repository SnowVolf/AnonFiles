package ru.svolf.anonfiles.api

import com.google.gson.annotations.SerializedName

/*
 * Created by SVolf on 02.02.2023, 21:45
 * This file is a part of "AnonFiles" project
 */
/**
 * Main class that represents service response
 * @param status status of response. If true - file exists, and you should get data from ApiFile object.
 *               if false - file does not exist, ApiFile object is null. You should handle error from ApiError object
 * @param data ApiFile object (null if status == false)
 * @param error ApiError object (null if status == true)
 * @see ApiFile
 * @see ApiError
 */
data class AnonResponse(@SerializedName("status") val status: Boolean, @SerializedName("data")
val data: ApiFile? = null, @SerializedName("error") val error: ApiError? = null)
