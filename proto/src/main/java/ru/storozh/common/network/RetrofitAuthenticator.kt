package ru.storozh.common.network

import okhttp3.Authenticator

interface RetrofitAuthenticator<CD : RetrofitConnect<API>, API : RetrofitAPI> : Authenticator {
    @Suppress("PropertyName")
    val TAG: String
        get() = this.javaClass.name

    fun setService(_service: RetrofitService<CD, API>)
}