package ru.storozh.common.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

const val FOR_DATA = "FOR_DATA"

class ForDataInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        val originalResponse = chain.proceed(chain.request())

        if (originalRequest.header(FOR_DATA) != null) {
            val clippedRequest = originalRequest.newBuilder()
                .removeHeader(FOR_DATA)
                .build()

            return chain.proceed(clippedRequest).newBuilder()
                .header(FOR_DATA, originalRequest.header(FOR_DATA)!!)
                .build()
        }

        return originalResponse
    }
}