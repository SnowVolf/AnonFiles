package ru.svolf.anonfiles.data.repository.impl

import android.webkit.MimeTypeMap
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import ru.svolf.anonfiles.api.AnonApi
import ru.svolf.anonfiles.api.AnonResult
import ru.svolf.anonfiles.api.ApiError
import ru.svolf.anonfiles.data.repository.UploadRepository
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/*
 * Created by SVolf on 06.03.2023, 20:07
 * This file is a part of "AnonFiles" project
 */
@Singleton
class UploadRepositoryImpl @Inject constructor(private val api: AnonApi): UploadRepository {

	override suspend fun uploadFile(file: File): AnonResult {
		val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)
		return try {
			val fp = api.upload(
				MultipartBody.Part.createFormData("file", file.name, file.asRequestBody(mimeType?.toMediaType()))
			)
			if (fp.status) {
				AnonResult.Success(fp.data!!)
			} else {
				AnonResult.Error(fp.error!!)
			}
		} catch (e: IOException) {
			return AnonResult.Error(ApiError("", "", 0))
		} catch (e: HttpException) {
			return AnonResult.Error(ApiError("", "", 0))
		}
	}
}