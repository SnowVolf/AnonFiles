package ru.svolf.anonfiles.data

/*
 * Created by SVolf on 07.03.2023, 15:21
 * This file is a part of "AnonFiles" project
 */
interface FilesSource {
 suspend fun upload(uri: String)
}