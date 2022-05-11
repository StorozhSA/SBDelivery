package ru.skillbranch.sbdelivery.hilt

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.skillbranch.sbdelivery.AppSharedPreferencesHelper
import ru.storozh.common.AppSharedPreferences
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ModuleSharedPref {

    @Provides
    @Singleton
    fun provideAppSharedPrefs(@ApplicationContext appContext: Context): AppSharedPreferences {
        return AppSharedPreferencesHelper(appContext)
    }
}
