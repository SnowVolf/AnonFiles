package ru.svolf.anonfiles.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import ru.svolf.anonfiles.data.model.ConnectionVisor

/*
 * Created by SVolf on 05.03.2023, 10:58
 * This file is a part of "AnonFiles" project
 */
class NetworkStatusTracker(context: Context) {

	private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

	val networkStatus = callbackFlow {
		val networkCallback = object : ConnectivityManager.NetworkCallback() {
			override fun onAvailable(network: Network) {
				trySend(ConnectionVisor.Available).isSuccess
			}

			override fun onLost(network: Network) {
				trySend(ConnectionVisor.Unavailable).isSuccess
			}

			override fun onUnavailable() {
				trySend(ConnectionVisor.Unavailable).isSuccess
			}
		}

		val request = NetworkRequest.Builder()
			.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
			.build()

		connectivityManager.registerNetworkCallback(request, networkCallback)

		awaitClose {
			connectivityManager.unregisterNetworkCallback(networkCallback)
		}
	}

}

inline fun <Result> Flow<ConnectionVisor>.map(
	crossinline onUnavailable: suspend  () -> Result,
	crossinline onAvailable: suspend  () -> Result,
): Flow<Result> = map { status ->
	when(status) {
		ConnectionVisor.Unavailable -> onUnavailable()
		ConnectionVisor.Available -> onAvailable()
	}
}