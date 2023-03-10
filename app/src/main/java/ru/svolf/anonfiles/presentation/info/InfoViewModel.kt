package ru.svolf.anonfiles.presentation.info

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.svolf.anonfiles.api.AnonResponse
import ru.svolf.anonfiles.api.AnonResult
import ru.svolf.anonfiles.api.ApiFile
import ru.svolf.anonfiles.api.ModeStrategy
import ru.svolf.anonfiles.data.repository.HistoryRepository
import ru.svolf.anonfiles.data.repository.NetworkRepository
import ru.svolf.anonfiles.service.FileDownloadWorker
import ru.svolf.anonfiles.service.FileUploadWorker
import ru.svolf.anonfiles.util._string
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
	private val _statusState = MutableStateFlow(_string.subtitle_welcome)
	val statusSate = _statusState.asStateFlow()


	fun fetchInfoForFile(link: String) {
		Timber.d(link)
		viewModelScope.launch {
			_loadingState.value = true
			networkRepository.safeApiCall(link)
				.collect { result ->
					_responseState.emit(result)
					_loadingState.value = false
				}
		}
	}

	fun downloadWithWorkManager(file: ApiFile) {
		val data = Data.Builder()

		data.apply {
			putString(FileDownloadWorker.KEY_FILE_NAME, file.fileName.substringBeforeLast('_'))
			putString(FileDownloadWorker.KEY_FILE_URL, file.fullUrl)
			putString(FileDownloadWorker.KEY_FILE_TYPE, file.fileName.substringAfterLast('_'))
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

		parseDownloadResult(worker.getWorkInfoByIdLiveData(fileWorker.id).asFlow())
		viewModelScope.launch {
			historyRepository.saveToHistory(file.fullUrl, file.sizeReadable, true)
		}
	}

	private fun parseDownloadResult(flow: Flow<WorkInfo>) {
		flow.map { it.state }
			.onEach {
				_statusState.value = when (it) {
					WorkInfo.State.SUCCEEDED -> _string.states_success
					WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING -> _string.state_running
					WorkInfo.State.CANCELLED, WorkInfo.State.BLOCKED -> _string.state_cancelled
					else -> _string.state_error
				}
				_loadingState.value = it == WorkInfo.State.ENQUEUED || it == WorkInfo.State.RUNNING
			}
			.launchIn(viewModelScope)
	}

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

		parseUploadResult(resultedFlow)
	}

	private fun parseUploadResult(flow: Flow<WorkInfo>) {
		val statusMessages = mapOf(
			WorkInfo.State.SUCCEEDED to _string.states_success,
			WorkInfo.State.RUNNING to _string.state_running,
			WorkInfo.State.ENQUEUED to _string.state_running,
			WorkInfo.State.CANCELLED to _string.state_cancelled,
			WorkInfo.State.BLOCKED to _string.state_cancelled,
		)

		viewModelScope.launch {
			flow.collect { workInfo ->
				val state = workInfo.state
				val statusMsg = statusMessages[state] ?: _string.state_error
				val isLoading = state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED

				_loadingState.value = isLoading
				_statusState.value = statusMsg

				if (state == WorkInfo.State.SUCCEEDED) {
					val outputData = workInfo.outputData
					val serverResponse = outputData.getString(FileUploadWorker.KEY_SERVER_RESPONSE)
					val obj = Gson().fromJson(serverResponse, AnonResponse::class.java)
					if (obj.status) {
						_responseState.emit(AnonResult.Success(obj.data!!, ModeStrategy.UPLOAD))
						historyRepository.saveToHistory(obj.data!!.fullUrl, obj.data!!.sizeReadable, true)
					} else {
						_responseState.emit(AnonResult.Error(obj.error!!, ModeStrategy.UPLOAD))
					}
				}
			}
		}
	}

}