package ru.storozh.common.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.*
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.storozh.common.network.NetworkMonitor.NetworkType

@Suppress("DEPRECATION")
class NetworkMonitorImpl(private val context: Context) : NetworkMonitor {
    private var _isConnected: Boolean = false
    override fun isConnected(): Boolean = this._isConnected
    private val _isConnectedLive = MutableLiveData(false)
    private val _networkTypeLive = MutableLiveData(NetworkType.NONE)
    override val isConnected: LiveData<Boolean> = _isConnectedLive
    override val networkType: LiveData<NetworkType> = _networkTypeLive

    private val cm: ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    init {
        registerNetworkMonitor()
    }

    @SuppressLint("MissingPermission")
    private fun registerNetworkMonitor() {
        _networkTypeLive.postValue(networkType(cm))

        cm.registerNetworkCallback(
            NetworkRequest.Builder().build(),
            object : ConnectivityManager.NetworkCallback() {
                override fun onCapabilitiesChanged(network: Network, nc: NetworkCapabilities) {
                    _networkTypeLive.postValue(networkType(cm))
                }

                override fun onLost(network: Network) {
                    _isConnected = false
                    _isConnectedLive.postValue(false)
                    _networkTypeLive.postValue(NetworkType.NONE)
                }

                override fun onAvailable(network: Network) {
                    _isConnected = true
                    _isConnectedLive.postValue(true)
                }
            }
        )
    }

    @SuppressLint("MissingPermission")
    private fun networkType(cm: ConnectivityManager): NetworkType {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            obtainNetworkType(cm.activeNetwork?.let { cm.getNetworkCapabilities(it) })
        } else {
            obtainNetworkType(cm.activeNetworkInfo)
        }
    }

    private fun obtainNetworkType(nc: NetworkCapabilities?): NetworkType = when {
        nc == null -> NetworkType.NONE
        nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
        nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.CELLULAR
        else -> NetworkType.UNKNOWN
    }

    private fun obtainNetworkType(ni: NetworkInfo?): NetworkType = when {
        ni == null -> NetworkType.NONE
        ni.type == TYPE_WIFI -> NetworkType.WIFI
        ni.type == TYPE_MOBILE -> NetworkType.CELLULAR
        else -> NetworkType.UNKNOWN
    }

}

