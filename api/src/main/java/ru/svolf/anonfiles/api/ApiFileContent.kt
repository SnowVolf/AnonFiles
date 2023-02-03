package ru.svolf.anonfiles.api

import kotlinx.serialization.SerialName

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
@kotlinx.serialization.Serializable
data class ApiFileContent(@SerialName("url") val url: FileUrl, @SerialName("metadata") val metadata: FileMetadata)
