package ru.skillbranch.sbdelivery.hilt

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
object ModuleStatesFeature {

    /* @InternalCoroutinesApi
     @Provides
     fun provideDishFeatureState(pref: AppSharedPreferences): DishFeature.State {
         val prefs = pref as AppSharedPreferencesHelper
         return DishFeature.State(
             //isFavorite = (it as DishV).favorite,
             isUserAuth = prefs.userIsAuth
         )
     }*/
}
