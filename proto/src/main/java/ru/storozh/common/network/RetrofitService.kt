package ru.storozh.common.network

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import retrofit2.Response

@Suppress("PropertyName")
interface RetrofitService<CD : RetrofitConnect<API>, API : RetrofitAPI> {
    val api: API

    fun <OUT> send(
        out: MutableSharedFlow<Result<OUT>>,
        handler: CoroutineExceptionHandler,
        match: Set<Consider>,
        progressBarFlag: MutableSharedFlow<Boolean>,
        scope: CoroutineScope,
        api: suspend () -> Response<OUT>
    ): Job
}