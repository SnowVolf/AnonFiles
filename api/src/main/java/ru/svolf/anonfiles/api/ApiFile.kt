package ru.svolf.anonfiles.api

import com.google.gson.annotations.SerializedName


/*
 * Created by SVolf on 03.02.2023, 10:59
 * This file is a part of "AnonFiles" project
 */
data class ApiFile(@SerializedName("file") val file: ApiFileContent) {

	val fullUrl get() = file.url.full

	val fileName get() = file.metadata.name

	val sizeReadable get() = file.metadata.size.readable

}
