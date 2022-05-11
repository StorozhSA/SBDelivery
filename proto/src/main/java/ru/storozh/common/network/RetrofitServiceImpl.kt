package ru.storozh.common.network


import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import ru.storozh.BuildConfig
import ru.storozh.common.AppException
import ru.storozh.common.network.ConsiderVariants.*
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit


class RetrofitServiceImpl<CD : RetrofitConnect<API>, API : RetrofitAPI>(
    connect: CD,
    cache: Cache? = null,
    authToken: RetrofitAuthenticator<CD, API>? = null,
    interceptorsApp: Set<Interceptor> = setOf(),
    interceptorsNet: Set<Interceptor> = setOf()
) : RetrofitService<CD, API> {

    override val api: API by lazy {
        val clientBuilder = OkHttpClient.Builder()

        clientBuilder.addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })

        if (cache != null) {
            clientBuilder.cache(cache)
        }
        if (authToken != null) {
            authToken.setService(this)
            clientBuilder.authenticator(authToken)
        }

        interceptorsApp.forEach {
            clientBuilder.addInterceptor(it)
        }
        interceptorsNet.forEach {
            clientBuilder.addNetworkInterceptor(it)
        }

        clientBuilder
            .connectTimeout(connect.timeoutConnect, TimeUnit.MILLISECONDS)
            .readTimeout(connect.timeoutRead, TimeUnit.MILLISECONDS)
            .writeTimeout(connect.timeoutWrite, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(connect.retryOnConnectionFailure)
            .build()

        val retrofit = Retrofit
            .Builder()
            .client(clientBuilder.build())
            .baseUrl(connect.baseUrl)
            //.addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .addConverterFactory(JacksonConverterFactory.create(ObjectMapper()))
            .build()

        retrofit.create(connect.api)
    }

    override fun <OUT> send(
        out: MutableSharedFlow<Result<OUT>>,
        handler: CoroutineExceptionHandler,
        match: Set<Consider>,
        progressBarFlag: MutableSharedFlow<Boolean>,
        scope: CoroutineScope,
        api: suspend () -> Response<OUT>
    ): Job {
        return scope.launch(Dispatchers.IO + handler) {
            val zeroConsider = match.firstOrNull { it.httpCode == 0 } ?: Consider(E, 0, "E_UNKNOWN")

            // Show progress bar event
            progressBarFlag.emit(true)

            if (BuildConfig.DEBUG) {
                //delay(500)
            }

            try {
                val response = api()
                match.firstOrNull { it.httpCode == response.code() }?.let {
                    when (it.variant) {
                        Y -> out.emit(Result.Success.Value(response, it.msg))
                        N -> out.emit(Result.Success.Empty(response, it.msg))
                        E -> {
                            out.emit(Result.Failure(response, it.msg))
                            throwEx(response, it.msg)
                        }
                    }
                } ?: run {
                    out.emit(Result.Failure(response, zeroConsider.msg))
                    throwEx(response, zeroConsider.msg)
                }
            } catch (ex: AppException) {
                throw ex
            } catch (ex: SocketTimeoutException) {
                out.emit(Result.Failure(null, "E_TIMEOUT_SOCKET", ex))
                throw AppException("E_TIMEOUT_SOCKET", ex.localizedMessage ?: "", ex)
            } catch (ex: IOException) {
                out.emit(Result.Failure(null, "E_NOT_CONNECTED", ex))
                throw AppException("E_NOT_CONNECTED", ex.localizedMessage ?: "", ex)
            } catch (ex: Exception) {
                out.emit(Result.Failure(null, zeroConsider.msg, ex))
                throw AppException(zeroConsider.msg, ex.localizedMessage ?: "", ex)
            } finally {
                progressBarFlag.emit(false)
            }
        }
    }

    private fun <OUT> throwEx(outData: Response<OUT>, error: String = "E_UNKNOWN") {
        throw AppException(
            _error = error,
            _errorHttp = outData.code(),
            message = "Retrofit error: res = $outData"
        )
    }
}