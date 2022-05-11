package ru.skillbranch.sbdelivery.ui.login

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.skillbranch.sbdelivery.AppSharedPreferencesHelper
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.data.EndPointsImpl
import ru.skillbranch.sbdelivery.data.RepoNetwork
import ru.storozh.common.AppSharedPreferences
import ru.storozh.common.EndPoints
import ru.storozh.common.Nav
import ru.storozh.common.validation.ValidString
import ru.storozh.common.validation.isNotEmpty
import ru.storozh.extensions.isAllTrue
import ru.storozh.extensions.update
import ru.storozh.models.delivery.network.domains.ReqLogin
import ru.storozh.ui.ProtoViewModel
import ru.storozh.ui.ProtoViewModelState
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    initState: State,
    @ApplicationContext context: Context,
    savedStateHandle: SavedStateHandle,
    private val network: RepoNetwork,
    point: EndPoints,
    pref: AppSharedPreferences
) : ProtoViewModel<LoginViewModel.State>(initState, context, savedStateHandle, point) {
    val points by lazy { point as EndPointsImpl }
    val prefs by lazy { pref as AppSharedPreferencesHelper }

    init {
        // Redirect to profile if auth
        if (prefs.userIsAuth) {
            navigate(Nav.To(R.id.action_nav_profile_login_to_nav_profile))
        }
    }

    fun login() {
        state.value?.let {
            network.login(
                ReqLogin(
                    email = it.email,
                    password = it.passw,
                ),
                points.mutableResLoginFlow(),
                viewModelScope
            )
        }
    }

    fun lock() = state.update { it.copy(isEnable = false) }
    fun unlock() = state.update { it.copy(isEnable = true) }
    fun isValidState() = state.value?.isValidState() ?: false

    data class State(
        val email: String = "",
        val passw: String = "",
        val isEnable: Boolean = false,
        val isNotEmptyEmail: Boolean = false,
        val isNotEmptyPassword: Boolean = false
    ) : ProtoViewModelState {
        override fun isValidState() = isAllTrue(
            isNotEmptyEmail,
            isNotEmptyPassword
        )
    }

    override fun checkState(): Boolean {
        state.update {
            it.copy(
                isNotEmptyEmail = ValidString.isNotEmpty(it.email),
                isNotEmptyPassword = ValidString.isNotEmpty(it.passw)
            )
        }
        return state.value!!.isValidState()
    }
}
