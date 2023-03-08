package ru.svolf.anonfiles.api

import com.google.gson.annotations.SerializedName

/*
 * Created by SVolf on 02.02.2023, 21:22
 * This file is a part of "AnonFiles" project
 */
/**
 * Represents full information for given file
 * @param url FileUrl object that contains download links
 * @param metadata FileMetadata object which contains file properties, such as name and size
 * @see FileUrl
 * @see FileMetadata
 */
data class ApiFileContent(@SerializedName("url") val url: FileUrl, @SerializedName("metadata") val metadata: FileMetadata)
