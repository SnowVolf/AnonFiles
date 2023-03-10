package ru.svolf.anonfiles.data.repository

import kotlinx.coroutines.flow.Flow
import ru.svolf.anonfiles.api.AnonResult

/*
 * Created by SVolf on 06.03.2023, 20:39
 * This file is a part of "AnonFiles" project
 */
interface NetworkRepository {
	suspend fun safeApiCall(url: String): Flow<AnonResult>

}