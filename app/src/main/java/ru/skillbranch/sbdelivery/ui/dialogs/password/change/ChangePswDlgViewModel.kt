package ru.skillbranch.sbdelivery.ui.dialogs.password.change

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
import ru.storozh.models.delivery.network.domains.ReqNewPassword
import ru.storozh.ui.ProtoViewModel
import ru.storozh.ui.ProtoViewModelState
import javax.inject.Inject

@HiltViewModel
class ChangePswDlgViewModel @Inject constructor(
    initState: State,
    @ApplicationContext context: Context,
    savedStateHandle: SavedStateHandle,
    private val network: RepoNetwork,
    point: EndPoints,
    pref: AppSharedPreferences
) : ProtoViewModel<ChangePswDlgViewModel.State>(initState, context, savedStateHandle, point) {
    val points by lazy { point as EndPointsImpl }
    val prefs by lazy { pref as AppSharedPreferencesHelper }

    fun changePassword() {
        state.value?.let {
            network.changePassword(
                ReqNewPassword(
                    oldPassword = it.passOld,
                    newPassword = it.passNew
                ),
                points.mutableResChangePasswordFlow(),
                viewModelScope
            )
        }
    }

    override fun checkState(): Boolean {
        state.update {
            it.copy(
                isNotEmptyPassOld = ValidString.isNotEmpty(it.passOld),
                isNotEmptyPassNew = ValidString.isNotEmpty(it.passNew)
            )
        }
        return state.value!!.isValidState()
    }

    // @Parcelize
    data class State(
        val passOld: String = "",
        val passNew: String = "",
        val isNotEmptyPassOld: Boolean = false,
        val isNotEmptyPassNew: Boolean = false,
    ) : ProtoViewModelState {
        override fun isValidState() = isAllTrue(isNotEmptyPassOld, isNotEmptyPassNew)
    }
}
