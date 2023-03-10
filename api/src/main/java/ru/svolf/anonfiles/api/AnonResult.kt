package ru.svolf.anonfiles.api

/*
 * Created by SVolf on 02.03.2023, 18:15
 * This file is a part of "AnonFiles" project
 */
sealed class AnonResult {

	data class Success(val data: ApiFile, val strategy: ModeStrategy): AnonResult()

	data class Error(val error: ApiError, val strategy: ModeStrategy): AnonResult()

	object Empty : AnonResult()
}
