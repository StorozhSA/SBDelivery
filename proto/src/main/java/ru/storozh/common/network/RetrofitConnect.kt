package ru.storozh.common.network

interface RetrofitConnect<T : RetrofitAPI> {
    val api: Class<T>
    val baseUrl: String
        get() = ""
    val cacheSize: Long
        get() = 10 * 1024 * 1024
    val timeoutConnect: Long
        get() = 2000
    val timeoutRead: Long
        get() = 2000
    val timeoutWrite: Long
        get() = 2000
    val retryOnConnectionFailure: Boolean
        get() = false
}