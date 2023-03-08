package ru.svolf.anonfiles.presentation.info

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.svolf.anonfiles.data.repository.HistoryRepository
import javax.inject.Inject

/*
 * Created by SVolf on 19.02.2023, 12:30
 * This file is a part of "AnonFiles" project
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(private val repository: HistoryRepository): ViewModel() {

	fun getItems() = repository.getAllItems()

	suspend fun put(link: String, sizeReadable: String) {
		withContext(Dispatchers.IO) {
			repository.saveToHistory(link, sizeReadable, false)
		}
	}


	override fun onCleared() {
		super.onCleared()
		repository.disconnect()
	}
}