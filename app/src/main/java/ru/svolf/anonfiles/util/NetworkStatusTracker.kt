package ru.svolf.anonfiles.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/*
 * Created by SVolf on 05.03.2023, 10:58
 * This file is a part of "AnonFiles" project
 */
class NetworkStatusTracker(context: Context): ConnectionObserver {

	private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

	override fun observe(): Flow<ConnectionObserver.State> {
		return callbackFlow {
			val networkCallback = object : ConnectivityManager.NetworkCallback() {
				override fun onAvailable(network: Network) {
					trySend(ConnectionObserver.State.Available).isSuccess
				}

				override fun onLosing(network: Network, maxMsToLive: Int) {
					trySend(ConnectionObserver.State.Losing).isSuccess
				}

				override fun onLost(network: Network) {
					trySend(ConnectionObserver.State.Lost).isSuccess
				}

				override fun onUnavailable() {
					trySend(ConnectionObserver.State.Unavailable).isSuccess
				}
			}

			val request = NetworkRequest.Builder()
				.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
				.build()

			connectivityManager.registerNetworkCallback(request, networkCallback)

			awaitClose {
				connectivityManager.unregisterNetworkCallback(networkCallback)
			}
		}.distinctUntilChanged()
	}

}