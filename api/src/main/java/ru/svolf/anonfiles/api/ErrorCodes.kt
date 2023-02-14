package ru.svolf.anonfiles.api

/*
 * Created by SVolf on 02.02.2023, 21:38
 * This file is a part of "AnonFiles" project
 */
object ErrorCodes {
 const val ERROR_FILE_NOT_PROVIDED = 10
 const val ERROR_FILE_EMPTY = 11
 const val ERROR_FILE_INVALID = 12
 const val ERROR_USER_MAX_FILES_PER_HOUR_REACHED = 20
 const val ERROR_USER_MAX_FILES_PER_DAY_REACHED = 21
 const val ERROR_USER_MAX_BYTES_PER_HOUR_REACHED = 22
 const val ERROR_USER_MAX_BYTES_PER_DAY_REACHED = 23
 const val ERROR_FILE_DISALLOWED_TYPE = 30
 const val ERROR_FILE_SIZE_EXCEEDED = 31
 const val ERROR_FILE_BANNED = 32
 const val STATUS_ERROR_SYSTEM_FAILURE = 40
 const val NOT_FOUND = 404
}