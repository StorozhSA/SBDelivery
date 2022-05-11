package ru.storozh.common.network

import androidx.lifecycle.LiveData

interface NetworkMonitor {
    fun isConnected(): Boolean
    val isConnected: LiveData<Boolean>
    val networkType: LiveData<NetworkType>

    enum class NetworkType {
        NONE, UNKNOWN, WIFI, CELLULAR
    }
}