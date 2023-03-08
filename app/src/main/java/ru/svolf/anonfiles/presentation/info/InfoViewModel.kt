package ru.svolf.anonfiles.presentation.info

import android.annotation.SuppressLint
import android.net.Uri
import androidx.lifecycle.*
import androidx.work.*
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.svolf.anonfiles.api.AnonResponse
import ru.svolf.anonfiles.api.AnonResult
import ru.svolf.anonfiles.api.ApiError
import ru.svolf.anonfiles.api.ApiFile
import ru.svolf.anonfiles.data.repository.HistoryRepository
import ru.svolf.anonfiles.data.repository.NetworkRepository
import ru.svolf.anonfiles.service.FileDownloadWorker
import ru.svolf.anonfiles.service.FileUploadWorker
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor(private val worker: WorkManager,
										private val networkRepository: NetworkRepository,
										private val historyRepository: HistoryRepository) : ViewModel() {

	private val _responseState = MutableStateFlow<AnonResult>(AnonResult.Empty)
	val responseState = _responseState.asStateFlow()

	private val _loadingState = MutableStateFlow(false)
	val loadingState = _loadingState.asStateFlow()

	fun getData(link: String) {
		Timber.d(link)
		_loadingState.value = true
		viewModelScope.launch {
			val data = async {
				networkRepository.safeApiCall(link)
			}
			_responseState.value = data.await()
			_loadingState.value = false
		}
	}

	@SuppressLint("RestrictedApi")
	fun downloadWithWorkManager(file: ApiFile) : LiveData<WorkInfo> {
		val data = Data.Builder()

		data.apply {
			putString(FileDownloadWorker.KEY_FILE_NAME, file.file.metadata.name.substringBeforeLast('_'))
			putString(FileDownloadWorker.KEY_FILE_URL, file.file.url.full)
			putString(FileDownloadWorker.KEY_FILE_TYPE, file.file.metadata.name.substringAfterLast('_'))
		}
		val constraints = Constraints.Builder()
			.setRequiredNetworkType(NetworkType.CONNECTED)
			.setRequiresStorageNotLow(true)
			.build()

		val fileWorker = OneTimeWorkRequestBuilder<FileDownloadWorker>()
			.setConstraints(constraints)
			.setInputData(data.build())
			.addTag("AnonFilesDownloadWorker")
			.build()

		worker.enqueueUniqueWork("AnonFilesWork_${System.currentTimeMillis()}", ExistingWorkPolicy.APPEND_OR_REPLACE, fileWorker)

		return worker.getWorkInfoByIdLiveData(fileWorker.id)
	}

	@SuppressLint("RestrictedApi")
	fun uploadWithWorkManager(uri: Uri) {
		val data = Data.Builder()

		data.apply {
			putString(FileUploadWorker.KEY_FILE_URI, uri.toString())
		}

		val constraints = Constraints.Builder()
			.setRequiredNetworkType(NetworkType.CONNECTED)
			.setRequiresStorageNotLow(true)
			.build()

		val fileWorker = OneTimeWorkRequestBuilder<FileUploadWorker>()
			.setConstraints(constraints)
			.setInputData(data.build())
			.addTag("AnonFilesUploadWorker")
			.build()

		worker.enqueueUniqueWork("AnonFilesWork_${System.currentTimeMillis()}", ExistingWorkPolicy.APPEND_OR_REPLACE, fileWorker)
		val resultedFlow = worker.getWorkInfoByIdLiveData(fileWorker.id).asFlow()

		parseResult(resultedFlow)
	}

	private fun parseResult(flow: Flow<WorkInfo>) {
		viewModelScope.launch {
			flow.collect {
				when (it.state) {
					WorkInfo.State.SUCCEEDED -> {
						val result = it.outputData.getString(FileUploadWorker.KEY_SERVER_RESPONSE)
						val obj = Gson().fromJson(result, AnonResponse::class.java)
						if (obj.status) {
							_responseState.value = AnonResult.Success(obj.data!!)
							historyRepository.saveToHistory(obj.data!!.fullUrl, obj.data!!.sizeReadable, true)
						} else {
							_responseState.value = AnonResult.Error(obj.error!!)
						}
					}
					WorkInfo.State.RUNNING, WorkInfo.State.ENQUEUED -> {
						_responseState.value = AnonResult.Empty
					}
					else -> {
						_responseState.value = AnonResult.Error(ApiError("ERROR", "FAILED_TO_UPLOAD", 11))
					}
				}
			}
		}
	}

}