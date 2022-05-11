package ru.skillbranch.sbdelivery.ui.recovery2

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
import ru.storozh.models.delivery.network.domains.ReqRecoveryCode
import ru.storozh.ui.ProtoViewModel
import ru.storozh.ui.ProtoViewModelState
import javax.inject.Inject

@HiltViewModel
class Recovery2ViewModel @Inject constructor(
    initState: State,
    @ApplicationContext context: Context,
    savedStateHandle: SavedStateHandle,
    private val network: RepoNetwork,
    point: EndPoints,
    pref: AppSharedPreferences
) : ProtoViewModel<Recovery2ViewModel.State>(initState, context, savedStateHandle, point) {
    val points by lazy { point as EndPointsImpl }
    val prefs by lazy { pref as AppSharedPreferencesHelper }

    fun lock() = state.update { it.copy(isEnable = false) }
    fun unlock() = state.update { it.copy(isEnable = true) }
    fun recovery2() {
        state.value?.let {
            network.recovery(
                ReqRecoveryCode(code = it.code()),
                points.mutableRecovery2PasswordFlow(),
                viewModelScope
            )
        }
    }

    data class State(
        val d1: String = "",
        val d2: String = "",
        val d3: String = "",
        val d4: String = "",
        val isEnable: Boolean = false,
        val isNotEmpty: Boolean = false,
        val isEnableD1: Boolean = false,
        val isEnableD2: Boolean = false,
        val isEnableD3: Boolean = false,
        val isEnableD4: Boolean = false
    ) : ProtoViewModelState {
        override fun isValidState() = isNotEmpty
        fun code() = "${d1}${d2}${d3}$d4"
    }

    override fun checkState(): Boolean {
        state.update {
            it.copy(
                isNotEmpty = (
                        ValidString.isNotEmpty(it.d1) &&
                                ValidString.isNotEmpty(it.d2) &&
                                ValidString.isNotEmpty(it.d3) &&
                                ValidString.isNotEmpty(it.d4)
                        ),
                isEnableD1 = ValidString.isNotEmpty(it.d1),
                isEnableD2 = ValidString.isNotEmpty(it.d2),
                isEnableD3 = ValidString.isNotEmpty(it.d3),
                isEnableD4 = ValidString.isNotEmpty(it.d4),
            )
        }
        return state.value!!.isValidState()
    }
}
