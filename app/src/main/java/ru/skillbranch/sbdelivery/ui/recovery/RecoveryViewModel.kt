package ru.skillbranch.sbdelivery.ui.recovery

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.skillbranch.sbdelivery.AppSharedPreferencesHelper
import ru.skillbranch.sbdelivery.data.EndPointsImpl
import ru.skillbranch.sbdelivery.data.RepoNetwork
import ru.storozh.common.AppSharedPreferences
import ru.storozh.common.EndPoints
import ru.storozh.common.validation.ValidString
import ru.storozh.common.validation.isNotEmpty
import ru.storozh.extensions.update
import ru.storozh.models.delivery.network.domains.ReqRecoveryEmail
import ru.storozh.ui.ProtoViewModel
import ru.storozh.ui.ProtoViewModelState
import javax.inject.Inject

@HiltViewModel
class RecoveryViewModel @Inject constructor(
    initState: State,
    @ApplicationContext context: Context,
    savedStateHandle: SavedStateHandle,
    private val network: RepoNetwork,
    point: EndPoints,
    pref: AppSharedPreferences
) : ProtoViewModel<RecoveryViewModel.State>(initState, context, savedStateHandle, point) {
    val points by lazy { point as EndPointsImpl }
    val prefs by lazy { pref as AppSharedPreferencesHelper }

    fun lock() = state.update { it.copy(isEnable = false) }
    fun unlock() = state.update { it.copy(isEnable = true) }
    fun etalon() = State(email = prefs.userEmail, isEnable = true)
    fun recovery() {
        state.value?.let {
            network.recovery(
                ReqRecoveryEmail(email = it.email),
                points.mutableRecovery1PasswordFlow(),
                viewModelScope
            )
        }
    }

    // @Parcelize
    data class State(
        val email: String = "",
        val isEnable: Boolean = false,
        val isNotEmptyEmail: Boolean = false
    ) : ProtoViewModelState {
        override fun isValidState() = isNotEmptyEmail
    }

    override fun checkState(): Boolean {
        state.update { it.copy(isNotEmptyEmail = ValidString.isNotEmpty(it.email)) }
        return state.value!!.isValidState()
    }
}
