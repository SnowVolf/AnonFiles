package ru.svolf.anonfiles.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*
 * Created by SVolf on 02.02.2023, 20:59
 * This file is a part of "AnonFiles" project
 */
/**
 * Represents Meta-Data of the file
 * @param id File id
 * @param name Readable file name
 * @param size FileSize object
 * @see FileSize
 */
@Serializable
data class FileMetadata(@SerialName("id") val id: String, @SerialName("name") val name: String, @SerialName("size") val size: FileSize)
