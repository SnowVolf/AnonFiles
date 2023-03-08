package ru.svolf.anonfiles.service

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.svolf.anonfiles.api.AnonApi
import ru.svolf.anonfiles.util._drawable
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException

/*
 * Created by SVolf on 07.03.2023, 16:34
 * This file is a part of "AnonFiles" project
 */
@HiltWorker
class FileUploadWorker @AssistedInject constructor(
	@Assisted private val context: Context,
	@Assisted private val workerParameters: WorkerParameters,
	private val api: AnonApi,
	private val worker: WorkManager
): CoroutineWorker(context, workerParameters) {

	private val builder = NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID)
	private val notificatorId = NotificationConstants.UPLOAD_NOTIFICATION_ID

	companion object {
		// uri файла который выбрал юзер
		const val KEY_FILE_URI = "file_uri"
		// json ответ от сервера в виде строки
		const val KEY_SERVER_RESPONSE = "server_response"
	}

	override suspend fun doWork(): Result {
		val rawUri = inputData.getString(KEY_FILE_URI)?.toUri()
		val result = withContext(Dispatchers.IO){
			extractFile(rawUri)
		}
		return result?.let {
			val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(File(it.name).extension)
			Timber.d("Get NAME = " + it.name)
			startForegroundService(it.name)
			val parted = MultipartBody.Part.createFormData("file", it.name, it.bytes.toRequestBody(mimeType?.toMediaType()))
			return try {
				val upload = api.upload(parted)
				Result.success(workDataOf( KEY_SERVER_RESPONSE to Gson().toJson(upload)))
			} catch (ex: SocketTimeoutException) {
				if (runAttemptCount < 3)
					Result.retry()
				else
					Result.failure()
			}
		} ?: Result.failure()
	}

	private fun extractFile(rawUri: Uri?): ReadedResult? {
		val resolver = context.contentResolver
		rawUri?.let {
			try {
				resolver.query(rawUri, null, null, null)!!.use { cursor ->
					// очень маловероятно, но хуй знает...
					if (cursor.count == 0) throw Exception("Cannot find files that matches given URI!")
					cursor.moveToFirst()
					val fileNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
					val fileName = cursor.getString(fileNameIndex)
					val contentBytes = resolver.openInputStream(rawUri)?.use {
						//it.buffered(1024 * 1024 * 4)
						it.readBytes()
					}
					return ReadedResult(fileName, contentBytes!!)
				}
			} catch (ex: IOException) {
				ex.printStackTrace()
				return null
			}
		}
		return null
	}

	private suspend fun startForegroundService(fileName: String) {
		setForeground(
			ForegroundInfo(
				notificatorId,
				builder.setSmallIcon(IconCompat.createWithResource(context, _drawable.ic_download))
					.setContentTitle("Uploading $fileName")
					.setOngoing(true)
					.setProgress(0, 0, true)
					.addAction(
						NotificationCompat.Action(
						IconCompat.createWithResource(context, _drawable.ic_cancel_download),
						context.getString(android.R.string.cancel),
						worker.createCancelPendingIntent(id))
					).build()
			)
		)
	}

	class ReadedResult(val name: String, val bytes: ByteArray)

}