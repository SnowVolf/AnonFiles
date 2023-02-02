package ru.svolf.anonfiles.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*
 * Created by SVolf on 02.02.2023, 21:08
 * This file is a part of "AnonFiles" project
 */
/**
 * Represents URL for file
 * @param full an absolute URL to the file which given from Metadata
 * @param short URL which is relative to full URL (without file name)
 */
@Serializable
data class FileUrl(@SerialName("full") val full: String, @SerialName("short") val short: String)
