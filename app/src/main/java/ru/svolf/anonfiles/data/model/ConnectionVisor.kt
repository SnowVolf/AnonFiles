package ru.svolf.anonfiles.data.model

/*
 * Created by SVolf on 02.03.2023, 14:13
 * This file is a part of "AnonFiles" project
 */
sealed class ConnectionVisor {
	object Available : ConnectionVisor()
	object Unavailable : ConnectionVisor()
}
