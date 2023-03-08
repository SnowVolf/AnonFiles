package ru.svolf.anonfiles.util

import kotlinx.coroutines.flow.Flow


/*
 * Created by SVolf on 06.03.2023, 21:44
 * This file is a part of "AnonFiles" project
 */
interface ConnectionObserver {

	fun observe(): Flow<State>

	enum class State {
		Unavailable, Available, Losing, Lost
	}

}