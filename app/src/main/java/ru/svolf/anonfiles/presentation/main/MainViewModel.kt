package ru.svolf.anonfiles.presentation.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.svolf.anonfiles.util.NetworkStatusTracker
import javax.inject.Inject

/*
 * Created by SVolf on 05.03.2023, 11:40
 * This file is a part of "AnonFiles" project
 */
@HiltViewModel
class MainViewModel @Inject constructor(tracker: NetworkStatusTracker): ViewModel() {

	val connectionState = tracker.observe()

}