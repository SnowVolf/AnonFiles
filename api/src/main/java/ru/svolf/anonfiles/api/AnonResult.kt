package ru.svolf.anonfiles.api

/*
 * Created by SVolf on 02.03.2023, 18:15
 * This file is a part of "AnonFiles" project
 */
sealed class AnonResult {

	data class Success(val data: ApiFile): AnonResult()

	data class Error(val error: ApiError): AnonResult()

	object Empty : AnonResult()
}
