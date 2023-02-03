package ru.svolf.anonfiles.api

import kotlinx.serialization.SerialName

/*
 * Created by SVolf on 03.02.2023, 10:59
 * This file is a part of "AnonFiles" project
 */
@kotlinx.serialization.Serializable
data class ApiFile(@SerialName("file") val file: ApiFileContent)
