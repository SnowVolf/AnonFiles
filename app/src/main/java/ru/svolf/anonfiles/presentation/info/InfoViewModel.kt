package ru.svolf.anonfiles.presentation.info

import android.annotation.SuppressLint
import androidx.lifecycle.*
import androidx.work.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.svolf.anonfiles.api.AnonApi
import ru.svolf.anonfiles.api.AnonResult
import ru.svolf.anonfiles.api.ApiFile
import ru.svolf.anonfiles.data.repository.NetworkRepository
import ru.svolf.anonfiles.service.FileDownloadWorker
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor(private val worker: WorkManager) : ViewModel() {

	private val _responseState = MutableStateFlow<AnonResult>(AnonResult.Empty)
	val responseState: StateFlow<AnonResult> = _responseState

	private val _loadingState = MutableStateFlow(false)
	val loadingState: StateFlow<Boolean> = _loadingState

	fun getData(link: String, api: AnonApi) {
		Timber.d(link)
		_loadingState.value = true
		viewModelScope.launch {
			val data = async {
				NetworkRepository(api).safeApiCall(link)
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
			//.setRequiresBatteryNotLow(true)
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

}