package ru.svolf.anonfiles.api

import com.google.gson.annotations.SerializedName

/*
 * Created by SVolf on 02.02.2023, 21:08
 * This file is a part of "AnonFiles" project
 */
/**
 * Represents URL for file
 * @param full an absolute URL to the file which given from Metadata
 * @param short URL which is relative to full URL (without file name)
 */
data class FileUrl(@SerializedName("full") val full: String, @SerializedName("short") val short: String)
