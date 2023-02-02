package ru.svolf.anonfiles.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*
 * Created by SVolf on 02.02.2023, 20:52
 * This file is a part of "AnonFiles" project
 */
/**
 * Represents file size
 * @param bytes Physical file size in bytes
 * @param readable Readable file size which displayed to user
 */
@Serializable
data class FileSize(@SerialName("bytes") val bytes: Int, @SerialName("readable") val readable: String)
