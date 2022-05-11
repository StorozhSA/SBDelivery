package ru.skillbranch.sbdelivery.ui.main

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import ru.skillbranch.sbdelivery.AppSharedPreferencesHelper
import ru.skillbranch.sbdelivery.data.EndPointsImpl
import ru.storozh.common.AppSharedPreferences
import ru.storozh.common.EndPoints
import ru.storozh.extensions.update
import ru.storozh.ui.ProtoViewModel
import ru.storozh.ui.ProtoViewModelState
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    initState: State,
    @ApplicationContext context: Context,
    savedStateHandle: SavedStateHandle,
    point: EndPoints,
    pref: AppSharedPreferences
) : ProtoViewModel<MainViewModel.State>(initState, context, savedStateHandle, point) {
    val points by lazy { point as EndPointsImpl }
    val prefs by lazy { pref as AppSharedPreferencesHelper }

    init {
        // (over shared preferences change listener)
        viewModelScope.launch {
            points.sharedPref.iUserIsAuth().collect {
                state.update { state -> state.copy(isAuth = it) }
            }
        }

        // Init state from settings
        state.update {
            it.copy(
                name = "${prefs.userFirstName} ${prefs.userLastName}",
                email = prefs.userEmail,
                isAuth = prefs.userIsAuth,
                isRegister = prefs.userIsRegistered
            )
        }
    }

    override fun checkState(): Boolean = true

    data class State(
        val isAuth: Boolean = false,
        val isRegister: Boolean = false,
        val name: String = "name",
        val email: String = "email"
    ) : ProtoViewModelState
}
