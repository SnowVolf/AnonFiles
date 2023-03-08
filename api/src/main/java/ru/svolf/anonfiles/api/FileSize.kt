package ru.svolf.anonfiles.api

import com.google.gson.annotations.SerializedName


/*
 * Created by SVolf on 02.02.2023, 20:52
 * This file is a part of "AnonFiles" project
 */
/**
 * Represents file size
 * @param bytes Physical file size in bytes
 * @param readable Readable file size which displayed to user
 */
data class FileSize(@SerializedName("bytes") val bytes: Int, @SerializedName("readable") val readable: String)
