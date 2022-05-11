package ru.skillbranch.sbdelivery.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.ViewModelScoped
import ru.skillbranch.sbdelivery.AppSharedPreferencesHelper
import ru.skillbranch.sbdelivery.ui.categories.CategoriesPageFragment
import ru.skillbranch.sbdelivery.ui.categories.CategoriesPageViewModel
import ru.skillbranch.sbdelivery.ui.categories.CategoriesViewModel
import ru.skillbranch.sbdelivery.ui.dialogs.password.change.ChangePswDlgViewModel
import ru.skillbranch.sbdelivery.ui.favorites.FavoritesPageViewModel
import ru.skillbranch.sbdelivery.ui.home.HomeViewModel
import ru.skillbranch.sbdelivery.ui.login.LoginViewModel
import ru.skillbranch.sbdelivery.ui.main.MainViewModel
import ru.skillbranch.sbdelivery.ui.menu.MenuViewModel
import ru.skillbranch.sbdelivery.ui.profile.ProfileViewModel
import ru.skillbranch.sbdelivery.ui.recovery.RecoveryViewModel
import ru.skillbranch.sbdelivery.ui.recovery2.Recovery2ViewModel
import ru.skillbranch.sbdelivery.ui.recovery3.Recovery3ViewModel
import ru.skillbranch.sbdelivery.ui.register.RegisterViewModel
import ru.skillbranch.sbdelivery.ui.search.SearchViewModel
import ru.storozh.common.AppSharedPreferences

@InstallIn(ViewModelComponent::class)
@Module
object ModuleStatesViewModel {
    @Provides
    @ViewModelScoped
    fun provideMainViewModelState(): MainViewModel.State = MainViewModel.State()

    @Provides
    @ViewModelScoped
    fun provideProfileRegisterViewModelState(): RegisterViewModel.State = RegisterViewModel.State()

    @Provides
    @ViewModelScoped
    fun provideProfileLoginViewModelState(pref: AppSharedPreferences): LoginViewModel.State {
        val prefs = pref as AppSharedPreferencesHelper
        return LoginViewModel.State(
            email = prefs.userEmail
        )
    }

    @Provides
    @ViewModelScoped
    fun provideProfileViewModelState(pref: AppSharedPreferences): ProfileViewModel.State {
        val prefs = pref as AppSharedPreferencesHelper
        return ProfileViewModel.State(
            firstName = prefs.userFirstName,
            lastName = prefs.userLastName,
            email = prefs.userEmail
        )
    }

    @Provides
    @ViewModelScoped
    fun provideChangePasswordDialogViewModelState(): ChangePswDlgViewModel.State =
        ChangePswDlgViewModel.State()

    @Provides
    @ViewModelScoped
    fun provideRecovery1ViewModelState(): RecoveryViewModel.State = RecoveryViewModel.State()

    @Provides
    @ViewModelScoped
    fun provideRecovery2ViewModelState(): Recovery2ViewModel.State = Recovery2ViewModel.State()

    @Provides
    @ViewModelScoped
    fun provideRecovery3ViewModelState(): Recovery3ViewModel.State = Recovery3ViewModel.State()

    @Provides
    @ViewModelScoped
    fun provideMenuViewModelState(): MenuViewModel.State = MenuViewModel.State()

    @Provides
    @ViewModelScoped
    fun provideCategoriesViewModelState(): CategoriesViewModel.State = CategoriesViewModel.State()

    @Provides
    @ViewModelScoped
    fun provideCategoriesPageViewModelState(): CategoriesPageViewModel.State =
        CategoriesPageViewModel.State()

    @Provides
    @ViewModelScoped
    fun provideSearchViewModelState(): SearchViewModel.State = SearchViewModel.State()

    @Provides
    @ViewModelScoped
    fun provideHomeViewModelState(): HomeViewModel.State = HomeViewModel.State()

    @Provides
    @ViewModelScoped
    fun provideFavoritesPageViewModelState(): FavoritesPageViewModel.State =
        FavoritesPageViewModel.State()

    @Provides
    @ActivityScoped
    fun provideCategoriesPageFragment() = CategoriesPageFragment()
}
