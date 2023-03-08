package ru.svolf.anonfiles.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ru.svolf.anonfiles.api.AnonApi
import ru.svolf.anonfiles.util.UrlExtractor.findMatchedString
import ru.svolf.anonfiles.util._drawable
import ru.svolf.anonfiles.util._string
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.random.Random

/*
 * Created by SVolf on 04.03.2023, 10:34
 * This file is a part of "AnonFiles" project
 */
@HiltWorker
class FileDownloadWorker @AssistedInject constructor(
	@Assisted private val context: Context,
	@Assisted workerParameters: WorkerParameters,
	private val api: AnonApi,
	private val worker: WorkManager,
): CoroutineWorker(context, workerParameters) {

	private val builder = NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID)
	private val notificatorId = NotificationConstants.DOWNLOAD_NOTIFICATION_ID

	companion object {
		const val KEY_FILE_URL = "key_file_url"
		const val KEY_FILE_TYPE = "key_file_type"
		const val KEY_FILE_NAME = "key_file_name"
		const val KEY_FILE_URI = "key_file_uri"
	}

	@SuppressLint("MissingPermission")
	override suspend fun doWork(): Result {
		val fileUrl = inputData.getString(KEY_FILE_URL) ?: ""
		val fileName = inputData.getString(KEY_FILE_NAME) ?: ""
		val fileType = inputData.getString(KEY_FILE_TYPE) ?: ""

		Timber.e("Launch work with: url = $fileUrl, fileName = $fileName, fileType = $fileType")

		if ((fileUrl + fileName + fileType).isEmpty()){
			return Result.failure()
		}

		startForegroundService("${fileName}.${fileType}")
		val needUrl = doTempPrepositions(fileUrl)

		if (needUrl != null) {

			val uri = getSavedFileUri(fileName, fileType, needUrl, context)
			// File succesfully downloaded
			builder.setSmallIcon(IconCompat.createWithResource(context, _drawable.ic_download_done))
				.setContentTitle(context.getString(_string.notification_title_downloaded, fileName))
				.setOngoing(false)
				.setProgress(0, 0, false)
			uri?.let {
				builder.setContentIntent(createIntent(it))
				builder.setContentText(context.getString(_string.notification_msg_open))
				builder.clearActions()
			}
			NotificationManagerCompat.from(context).notify(Random.nextInt(), builder.build())
			return if (uri != null) {
				Result.success(workDataOf(KEY_FILE_URI to uri.toString()))
			} else {
				Result.failure()
			}
		} else {
			return Result.failure()
		}
	}

	private suspend fun getSavedFileUri(fileName: String, fileType: String, fileUrl: String?, context: Context): Uri? {
		delay(1000)
		val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileType) ?: ""

		if (mimeType.isEmpty() or fileUrl.isNullOrEmpty()) return null

		val downloaded = fileUrl?.let { api.downloadFile(it) }

		downloaded?.body()?.let { fileResponse ->
			// Write to file using SAF
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
				val contentValues = ContentValues().apply {
					put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
					put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
					put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
				}

				val resolver = context.contentResolver
				val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
				return try  {
					withContext(Dispatchers.IO) {
						fileResponse.byteStream().use { input ->
							resolver.openOutputStream(uri!!).use { output ->
								input.copyTo(output!!, 1024 * 1024 * 4) // 4 MB buffer
							}
						}
						uri
					}
				} catch (ex: IOException) {
					null
				}
			} else {
				// Legacy File IO
				withContext(Dispatchers.IO) {
					val target = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
					try {
						fileResponse.byteStream().use { input ->
							FileOutputStream(target).use { output ->
								input.copyTo(output, 1024 * 1024 * 4) // 4 MB buffer
							}
						}
					} catch (ex: IOException) {
						// прокидываю null чтоб задача корректно отменилась
						return@withContext null
					}
					return@withContext target.toUri()
				}
			}
		}
		return null
	}

	private suspend fun startForegroundService(fileName: String) {
		setForeground(
			ForegroundInfo(
				notificatorId,
				builder.setSmallIcon(IconCompat.createWithResource(context, _drawable.ic_download))
					.setContentTitle(context.getString(_string.notification_title_downloading, fileName))
					.setOngoing(true)
					.setProgress(0, 0, true)
					.addAction(NotificationCompat.Action(
						IconCompat.createWithResource(context, _drawable.ic_cancel_download),
						context.getString(android.R.string.cancel),
						worker.createCancelPendingIntent(id))
					).build()
			)
		)
	}

	/**
	 * Так как пидорасы из AnonFiles не придумали ничего лучше как давать в API ссылку на предпросмотр HTML страницы,
	 * сначала нужно загрузить эту html страницу и потом внутри нее найти уже саму ссылку
	 * @param htmlUrl url который надо загрузить
	 * @return найденный внутри HTML URL на файл или null
	*/
	private suspend fun doTempPrepositions(htmlUrl: String): String? {
		Timber.d("doTempPrepositions() string not empty = ${htmlUrl.isNotEmpty()}")
		val downld = api.downloadHtml(htmlUrl)
		val text = downld.body()?.charStream()?.readText()
		return findMatchedString(text!!)
	}

	/**
	 * Intent по которому можно будет открыть файл который юзер загрузил
	 */
	private fun createIntent(uri: Uri): PendingIntent {
		val srcIntent = Intent.createChooser(
			Intent(Intent.ACTION_VIEW, uri).apply {
				flags = Intent.FLAG_ACTIVITY_NEW_TASK
			}, null
		)
		return PendingIntent.getActivity(context, 100, srcIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
	}

}