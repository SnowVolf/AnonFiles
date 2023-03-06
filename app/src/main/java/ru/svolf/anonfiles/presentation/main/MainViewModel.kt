package ru.svolf.anonfiles.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import ru.svolf.anonfiles.util.NetworkStatusTracker
import ru.svolf.anonfiles.util.map
import javax.inject.Inject

/*
 * Created by SVolf on 05.03.2023, 11:40
 * This file is a part of "AnonFiles" project
 */
@HiltViewModel
class MainViewModel @Inject constructor(tracker: NetworkStatusTracker): ViewModel() {

	val networkState = tracker.networkStatus.map(onAvailable = { true }, onUnavailable = { false }).asLiveData(Dispatchers.IO)

}