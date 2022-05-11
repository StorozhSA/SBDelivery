package ru.skillbranch.sbdelivery.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.skillbranch.sbdelivery.data.EndPointsImpl
import ru.storozh.common.EndPoints
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ModuleEndPoints {
    @Provides
    @Singleton
    fun endPoints(): EndPoints = EndPointsImpl()
}
