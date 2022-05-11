package ru.skillbranch.sbdelivery.ui.recovery3

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
import ru.storozh.extensions.isAllTrue
import ru.storozh.extensions.update
import ru.storozh.models.delivery.network.domains.ReqRecoveryPassword
import ru.storozh.ui.ProtoViewModel
import ru.storozh.ui.ProtoViewModelState
import javax.inject.Inject

@HiltViewModel
class Recovery3ViewModel @Inject constructor(
    initState: State,
    @ApplicationContext context: Context,
    savedStateHandle: SavedStateHandle,
    private val network: RepoNetwork,
    point: EndPoints,
    pref: AppSharedPreferences
) : ProtoViewModel<Recovery3ViewModel.State>(initState, context, savedStateHandle, point) {
    val points by lazy { point as EndPointsImpl }
    val prefs by lazy { pref as AppSharedPreferencesHelper }

    fun lock() = state.update { it.copy(isEnable = false) }
    fun unlock() = state.update { it.copy(isEnable = true) }
    fun isValidState() = state.value?.isValidState() ?: false
    fun recovery3(code: String) {
        state.value?.let {
            network.recovery(
                ReqRecoveryPassword(
                    email = prefs.userEmail,
                    password = state.value!!.pass1,
                    code = code
                ),
                points.mutableRecovery3PasswordFlow(),
                viewModelScope
            )
        }
    }

    data class State(
        val pass1: String = "",
        val pass2: String = "",
        val isEnable: Boolean = false,
        val isNotEmptyPass1: Boolean = false,
        val isNotEmptyPass2: Boolean = false,
        val isEqualsPass: Boolean = false
    ) : ProtoViewModelState {
        override fun isValidState() = isAllTrue(isNotEmptyPass1, isNotEmptyPass2, isEqualsPass)
    }

    override fun checkState(): Boolean {
        state.update {
            it.copy(
                isEqualsPass = (it.pass1 == it.pass2),
                isNotEmptyPass1 = ValidString.isNotEmpty(it.pass1),
                isNotEmptyPass2 = ValidString.isNotEmpty(it.pass2)
            )
        }
        return state.value!!.isValidState()
    }
}
