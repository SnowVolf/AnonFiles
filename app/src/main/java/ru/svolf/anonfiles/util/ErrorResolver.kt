package ru.svolf.anonfiles.util

import ru.svolf.anonfiles.api.ErrorCodes

/*
 * Created by SVolf on 10.03.2023, 12:02
 * This file is a part of "AnonFiles" project
 */
object ErrorResolver {
	val localizedMessage =
		 mapOf(
			ErrorCodes.ERROR_FILE_INVALID to _string.err_file_invalid,
			ErrorCodes.ERROR_FILE_BANNED to _string.err_file_banned,
			ErrorCodes.ERROR_FILE_EMPTY to _string.err_file_empty,
			ErrorCodes.ERROR_FILE_NOT_PROVIDED to _string.err_file_provided,
			ErrorCodes.ERROR_FILE_DISALLOWED_TYPE to _string.err_file_disallowed,
			ErrorCodes.ERROR_FILE_SIZE_EXCEEDED to _string.err_size_exceeded,
			ErrorCodes.ERROR_USER_MAX_BYTES_PER_DAY_REACHED to _string.err_bytes_day,
			ErrorCodes.ERROR_USER_MAX_BYTES_PER_HOUR_REACHED to _string.err_bytes_hour,
			ErrorCodes.ERROR_USER_MAX_FILES_PER_DAY_REACHED to _string.err_files_day,
			ErrorCodes.ERROR_USER_MAX_FILES_PER_HOUR_REACHED to _string.err_files_hour,
			ErrorCodes.NOT_FOUND to _string.err_404,
			ErrorCodes.STATUS_ERROR_SYSTEM_FAILURE to _string.err_sys_failture,
		)
}
