package ru.storozh.common.network

import okhttp3.Headers
import okhttp3.internal.EMPTY_HEADERS
import retrofit2.Response

sealed class Result<out P>(val msg: String) {
    override fun toString() = "Data result -> $msg"
    abstract val techCode: Int
    abstract val techMessage: String
    abstract val techHeaders: Headers


    sealed class Success<P>(response: Response<P>, msg: String) : Result<P>(msg) {
        override val techCode: Int = response.code()
        override val techMessage: String = response.message()
        override val techHeaders: Headers = response.headers()

        class Value<P>(response: Response<P>, msg: String) : Success<P>(response, msg) {
            val payload: P = response.body()!!
            override fun toString() = "Data success -> $msg : [$payload]"
        }

        class Empty<P>(response: Response<P>, msg: String) : Success<P>(response, msg) {
            override fun toString() = "Data success. Not modified. -> $msg : []"
        }
    }

    /*class Nothing<P>(msg: String = "") : Result<P>(msg) {
        override val techCode: Int
            get() = 0
        override val techMessage: String
            get() = ""
        override val techHeaders: Headers
            get() = EMPTY_HEADERS

    }*/

    class Failure<P>(response: Response<P>?, msg: String, ex: Throwable? = null) :
        Result<P>(msg) {
        override val techCode: Int
        override val techMessage: String
        override val techHeaders: Headers
        override fun toString() = "Data error -> $msg : [$techMessage]"

        init {
            when {
                response != null -> {
                    techCode = response.code()
                    techMessage = response.message()
                    techHeaders = response.headers()
                }
                ex != null -> {
                    techCode = 0
                    techMessage = ex.localizedMessage ?: "Unknown error"
                    techHeaders = EMPTY_HEADERS
                }
                else -> {
                    techCode = 0
                    techMessage = "Unknown error"
                    techHeaders = EMPTY_HEADERS
                }
            }
        }
    }
}