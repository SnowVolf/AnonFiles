package ru.svolf.anonfiles.presentation.info

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import ru.svolf.anonfiles.api.AnonApi
import ru.svolf.anonfiles.api.AnonResponse
import ru.svolf.anonfiles.data.Repository
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor(private val api: AnonApi): ViewModel() {
	val responseData = MutableLiveData<AnonResponse>()
	val loadingState = MutableLiveData<Boolean>().apply { value = false }

	fun getData(link: String) {
		loadingState.value = true
		responseData.value = Repository(api).getData(link)
		Thread.sleep(5000)
		loadingState.value = false
	}
}