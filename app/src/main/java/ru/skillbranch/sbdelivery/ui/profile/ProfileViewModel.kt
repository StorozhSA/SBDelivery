package ru.skillbranch.sbdelivery.ui.profile

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import ru.skillbranch.sbdelivery.AppSharedPreferencesHelper
import ru.skillbranch.sbdelivery.data.EndPointsImpl
import ru.skillbranch.sbdelivery.data.RepoNetwork
import ru.storozh.common.AppSharedPreferences
import ru.storozh.common.EndPoints
import ru.storozh.common.Nav
import ru.storozh.common.validation.ValidString
import ru.storozh.common.validation.isNotEmpty
import ru.storozh.common.validation.isRUorEN
import ru.storozh.common.validation.isValidEmail
import ru.storozh.extensions.isAllTrue
import ru.storozh.extensions.update
import ru.storozh.models.delivery.network.domains.ReqUserProfile
import ru.storozh.ui.ProtoViewModel
import ru.storozh.ui.ProtoViewModelState
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    initState: State,
    @ApplicationContext context: Context,
    savedStateHandle: SavedStateHandle,
    private val network: RepoNetwork,
    point: EndPoints,
    pref: AppSharedPreferences
) : ProtoViewModel<ProfileViewModel.State>(initState, context, savedStateHandle, point) {
    val points by lazy { point as EndPointsImpl }
    val prefs by lazy { pref as AppSharedPreferencesHelper }

    init {
        // Redirect to back if auth->not auth
        viewModelScope.launch {
            points.sharedPref.iUserIsAuth().collect { if (!it) navigate(Nav.PopBackStack()) }
        }

        etalon()
    }

    fun lock() = state.update { it.copy(isEnable = false) }
    fun unlock() = state.update { it.copy(isEnable = true) }
    fun etalon() = State(
        firstName = prefs.userFirstName,
        lastName = prefs.userLastName,
        email = prefs.userEmail,
        isEnable = false
    )

    fun canSave(): Boolean = (
            etalon().copy(isEnable = true)
                .hashCode() != state.value.hashCode() && state.value!!.isValidState()
            )

    fun editProfile() {
        state.value?.let {
            network.profile(
                ReqUserProfile(
                    email = it.email,
                    firstName = it.firstName,
                    lastName = it.lastName
                ),
                points.mutableResUserProfileFlow(),
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
                isNotEmptyLastName = ValidString.isNotEmpty(it.lastName)
            )
        }
        return state.value!!.isValidState()
    }

    // @Parcelize
    data class State(
        val lastName: String = "",
        val firstName: String = "",
        val email: String = "",
        val isEnable: Boolean = false,
        val isValidLastName: Boolean = true,
        val isValidFirstName: Boolean = true,
        val isValidEmail: Boolean = true,
        val isNotEmptyLastName: Boolean = true,
        val isNotEmptyFirstName: Boolean = true,
    ) : ProtoViewModelState {
        override fun isValidState() = isAllTrue(
            isValidEmail,
            isValidFirstName,
            isValidLastName,
            isNotEmptyFirstName,
            isNotEmptyLastName
        )
    }
}
