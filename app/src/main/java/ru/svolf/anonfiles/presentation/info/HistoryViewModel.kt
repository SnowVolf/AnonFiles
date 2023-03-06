package ru.svolf.anonfiles.presentation.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.svolf.anonfiles.data.repository.HistoryRepository
import javax.inject.Inject

/*
 * Created by SVolf on 19.02.2023, 12:30
 * This file is a part of "AnonFiles" project
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(private val repository: HistoryRepository): ViewModel() {

	fun getItems() = repository.getAllItems()

	fun putAsDownloaded(link: String, sizeReadable: String): Unit = putDataItem(link, sizeReadable, false)

	fun putAsUploaded(link: String, sizeReadable: String) = putDataItem(link, sizeReadable, true)

	override fun onCleared() {
		super.onCleared()
		repository.disconnect()
	}

	private fun putDataItem(link: String, sizeReadable: String, uploaded: Boolean) {
		viewModelScope.launch {
			repository.saveToHistory(link, sizeReadable, uploaded)
		}
	}
}