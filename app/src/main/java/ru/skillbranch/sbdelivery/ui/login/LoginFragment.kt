package ru.skillbranch.sbdelivery.ui.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.databinding.FragmentLoginBinding
import ru.storozh.common.Nav
import ru.storozh.common.network.Result
import ru.storozh.delegates.Prop
import ru.storozh.extensions.*
import ru.storozh.ui.ProtoBinding
import ru.storozh.ui.ProtoFragment
import ru.storozh.ui.ProtoViewModelState

@AndroidEntryPoint
class LoginFragment :
    ProtoFragment<LoginViewModel, LoginFragment.Binding>(R.layout.fragment_login) {
    override val vm by viewModels<LoginViewModel>()
    override val bv by viewBinding(FragmentLoginBinding::bind)
    override val bs by lazy { Binding() }
    private val args: LoginFragmentArgs by navArgs()
    override fun onCreateSetup(view: View, bundle: Bundle?) {
        // E-mail
        bv.loginEtEmail.watch {
            vm.state.update { it.copy(email = this.toString()) }
            vm.checkState()
        }

        // Password
        bv.loginEtPassw.watch {
            vm.state.update { it.copy(passw = this.toString()) }
            vm.checkState()
        }

        // Login button
        bv.loginBtnLogin.setOnClickListener {
            hideKeyboard()
            vm.lock()
            vm.login()
        }

        // Registration button
        bv.loginBtnRegistration.setOnClickListener { vm.navigate(Nav.To(R.id.action_nav_profile_login_to_nav_profile_register)) }
        bv.loginBtnRegistration.isEnabled = !vm.prefs.userIsRegistered

        // Login result action
        launchRepeatOnStarted {
            vm.points.immutableResLoginFlow().collect {
                vm.unlock()
                when (it) {
                    is Result.Success.Value -> nc.navigate(R.id.action_nav_profile_login_to_nav_profile)
                    else -> logd("Failure login")
                }
            }
        }

        bv.loginTvPasswForgot.setOnClickListener {
            vm.navigate(Nav.To(R.id.action_global_nav_recovery1))
        }
    }

    // Привязка состояния модели
    inner class Binding : ProtoBinding() {
        private var email by Prop("") { bv.loginEtEmail.updateText(it) }
        private var passw by Prop("") { bv.loginEtPassw.updateText(it) }
        private var isEnable by Prop(false) {
            bv.loginTvPasswForgot.enable(it)
            bv.loginEtEmail.enable(it)
            bv.loginEtPassw.enable(it)
            bv.loginBtnLogin.enable(it)
            bv.loginBtnRegistration.enable(it && !vm.prefs.userIsRegistered)
        }
        private var isNotEmptyPassword by Prop(true) { bv.loginBtnLogin.enable(vm.isValidState()) }
        private var isNotEmptyEmail by Prop(true) { bv.loginBtnLogin.enable(vm.isValidState()) }
        override fun bind(data: ProtoViewModelState) {
            data as LoginViewModel.State
            data.email.also { email = it }
            data.passw.also { passw = it }
            data.isEnable.also { isEnable = it }
            data.isNotEmptyPassword.also { isNotEmptyPassword = it }
            data.isNotEmptyEmail.also { isNotEmptyEmail = it }
        }
    }
}
