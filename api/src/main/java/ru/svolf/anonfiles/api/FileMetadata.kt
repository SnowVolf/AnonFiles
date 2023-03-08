package ru.svolf.anonfiles.api

import com.google.gson.annotations.SerializedName


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

data class FileMetadata(@SerializedName("id") val id: String, @SerializedName("name") val name: String, @SerializedName("size") val size: FileSize)
