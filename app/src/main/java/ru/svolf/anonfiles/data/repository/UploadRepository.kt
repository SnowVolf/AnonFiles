package ru.svolf.anonfiles.data.repository

import ru.svolf.anonfiles.api.AnonResult
import java.io.File

/*
 * Created by SVolf on 06.03.2023, 19:57
 * This file is a part of "AnonFiles" project
 */
interface UploadRepository {

	suspend fun uploadFile(file: File): AnonResult

}