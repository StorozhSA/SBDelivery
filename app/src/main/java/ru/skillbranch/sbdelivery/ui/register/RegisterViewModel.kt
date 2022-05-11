package ru.skillbranch.sbdelivery.ui.register

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
import ru.storozh.common.validation.isRUorEN
import ru.storozh.common.validation.isValidEmail
import ru.storozh.extensions.isAllTrue
import ru.storozh.extensions.update
import ru.storozh.models.delivery.network.domains.ReqRegister
import ru.storozh.ui.ProtoViewModel
import ru.storozh.ui.ProtoViewModelState
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    initState: State,
    @ApplicationContext context: Context,
    savedStateHandle: SavedStateHandle,
    private val network: RepoNetwork,
    point: EndPoints,
    pref: AppSharedPreferences
) : ProtoViewModel<RegisterViewModel.State>(initState, context, savedStateHandle, point) {
    val points by lazy { point as EndPointsImpl }
    val prefs by lazy { pref as AppSharedPreferencesHelper }

    fun lock() = state.update { it.copy(isEnable = false) }
    fun unlock() = state.update { it.copy(isEnable = true) }
    fun isValidState() = state.value?.isValidState() ?: false

    fun register() {
        state.value?.let {
            network.register(
                ReqRegister(
                    email = it.email,
                    firstName = it.firstName,
                    lastName = it.lastName,
                    password = it.password,
                ),
                points.mutableResRegisterFlow(),
                viewModelScope
            )
        }
    }

    override fun checkState(): Boolean {
        state.update {
            it.copy(
                isValidFirstName = ValidString.isRUorEN(it.firstName),
                isValidLastName = ValidString.isRUorEN(it.lastName),
                isValidEmail = ValidString.isValidEmail(it.email),
                isNotEmptyFirstName = ValidString.isNotEmpty(it.firstName),
                isNotEmptyLastName = ValidString.isNotEmpty(it.lastName),
                isNotEmptyPassword = ValidString.isNotEmpty(it.password),
            )
        }
        return state.value!!.isValidState()
    }

    // @Parcelize
    data class State(
        val lastName: String = "",
        val firstName: String = "",
        val email: String = "",
        val password: String = "",
        val isEnable: Boolean = false,
        val isValidLastName: Boolean = true,
        val isValidFirstName: Boolean = true,
        val isValidEmail: Boolean = true,
        val isNotEmptyPassword: Boolean = true,
        val isNotEmptyLastName: Boolean = true,
        val isNotEmptyFirstName: Boolean = true,
    ) : ProtoViewModelState {
        override fun isValidState() = isAllTrue(
            isValidEmail,
            isValidFirstName,
            isValidLastName,
            isNotEmptyFirstName,
            isNotEmptyLastName,
            isNotEmptyPassword
        )
    }
}
