package ru.skillbranch.sbdelivery.hilt

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import ru.storozh.common.AppSharedPreferences
import ru.storozh.common.network.*
import ru.storozh.models.delivery.network.DeliveryAPI
import ru.storozh.models.delivery.network.DeliveryAuthenticator
import ru.storozh.models.delivery.network.DeliveryConnect
import java.io.File
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ModuleNetwork {

    @Provides
    @Singleton
    fun provideCacheForRetrofit(@ApplicationContext appContext: Context): Cache {
        return Cache(File(appContext.cacheDir, "http-cache"), 10 * 1024 * 1024) // 10 MB
    }

    @Provides
    @Singleton
    fun provideDeliveryAuthenticator(pref: AppSharedPreferences): RetrofitAuthenticator<DeliveryConnect, DeliveryAPI> {
        return DeliveryAuthenticator(pref)
    }

    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext appContext: Context): NetworkMonitor {
        return NetworkMonitorImpl(appContext)
    }

    @DeliveryServiceNetwork
    @Provides
    @Singleton
    fun provideNetworkDelivery(
        cache: Cache,
        auth: RetrofitAuthenticator<DeliveryConnect, DeliveryAPI>,
        @ApplicationContext appContext: Context
    ): RetrofitService<DeliveryConnect, DeliveryAPI> {
        return RetrofitServiceImpl(
            DeliveryConnect(),
            cache,
            auth,
            setOf(NetworkStatusInterceptor(appContext), ForDataInterceptor())
        )
        // return RetrofitServiceImpl(DeliveryConnect(), cache)
    }
}
