package ru.skillbranch.sbdelivery.hilt

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.storozh.models.delivery.database.DeliveryDatabaseService
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ModuleDataBase {

    @Provides
    @Singleton
    @DeliveryServiceDataBase
    fun provideDatabase(@ApplicationContext appContext: Context): DeliveryDatabaseService {
        return DeliveryDatabaseService.getInstance(appContext)
    }
}
