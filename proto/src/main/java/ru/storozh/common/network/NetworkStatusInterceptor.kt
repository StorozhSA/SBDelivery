package ru.storozh.common.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import ru.storozh.extensions.isNetworkAvailable
import java.io.IOException

class NetworkStatusInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!context.isNetworkAvailable) throw IOException("Network not connected")
        return chain.proceed(chain.request())
    }
}