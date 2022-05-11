package ru.skillbranch.sbdelivery.ui.register

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import dagger.hilt.android.AndroidEntryPoint
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.databinding.FragmentRegisterBinding
import ru.storozh.common.Nav
import ru.storozh.common.network.Result
import ru.storozh.common.validation.VCTextView
import ru.storozh.delegates.Prop
import ru.storozh.extensions.*
import ru.storozh.ui.ProtoBinding
import ru.storozh.ui.ProtoFragment
import ru.storozh.ui.ProtoViewModelState

@AndroidEntryPoint
class RegisterFragment :
    ProtoFragment<RegisterViewModel, RegisterFragment.Binding>(R.layout.fragment_register) {
    override val vm by viewModels<RegisterViewModel>()
    override val bv by viewBinding(FragmentRegisterBinding::bind)
    override val bs by lazy { Binding() }

    override fun onCreateSetup(view: View, bundle: Bundle?) {
        // Firstname
        bv.regEtFirstName.watch {
            vm.state.update { it.copy(firstName = this.toString()) }
            vm.checkState()
        }

        // Lastname
        bv.regEtLastName.watch {
            vm.state.update { it.copy(lastName = this.toString()) }
            vm.checkState()
        }

        // E-mail
        bv.regEtEmail.watch {
            vm.state.update { it.copy(email = this.toString()) }
            vm.checkState()
        }

        // Password
        bv.regEtPass.watch {
            vm.state.update { it.copy(password = this.toString()) }
            vm.checkState()
        }

        // Button Save
        bv.regBtnRegistration.setOnClickListener {
            hideKeyboard()
            if (vm.isValidState()) {
                vm.lock()
                vm.register()
            }
        }

        // Register result action
        lifecycleScope.launchWhenStarted {
            vm.points.immutableResRegisterFlow().collect {
                vm.unlock()
                when (it) {
                    is Result.Success.Value -> {
                        // Если предыдущий фрагмент был Логин переходим сразу в профиль
                        if (nc.previousBackStackEntry?.destination?.id ?: -1 == R.id.nav_profile_login) {
                            val options = NavOptions.Builder()
                                .setPopUpTo(R.id.nav_home, false)
                                .setLaunchSingleTop(true)
                                .build()
                            vm.navigate(Nav.To(R.id.nav_profile, null, options))
                        } else {
                            nc.popBackStack()
                        }
                    }
                    else -> logd("Failure register")
                }
            }
        }
    }

    // Привязка состояния модели
    @SuppressLint("ResourceAsColor", "ResourceType")
    inner class Binding : ProtoBinding() {
        private val fieldErrFirstName = VCTextView(bv.regTvErrFirstName)
        private val fieldErrLastName = VCTextView(bv.regTvErrLastName)

        private var firstName by Prop("") { bv.regEtFirstName.updateText(it) }
        private var lastName by Prop("") { bv.regEtLastName.updateText(it) }
        private var email by Prop("") { bv.regEtEmail.updateText(it) }
        private var password by Prop("") { bv.regEtPass.updateText(it) }
        private var isEnable by Prop(false) {
            bv.regEtFirstName.enable(it)
            bv.regEtLastName.enable(it)
            bv.regEtEmail.enable(it)
            bv.regEtPass.enable(it)
            bv.regBtnRegistration.enable(it)
        }
        private var isValidFirstName by Prop(true) {
            showControlFirstName(
                it,
                R.string.err_only_letters
            )
        }
        private var isNotEmptyFirstName by Prop(true) {
            showControlFirstName(
                it,
                R.string.err_empty_field
            )
        }
        private var isValidLastName by Prop(true) {
            showControlLastName(
                it,
                R.string.err_only_letters
            )
        }
        private var isNotEmptyLastName by Prop(true) {
            showControlLastName(
                it,
                R.string.err_empty_field
            )
        }
        private var isValidEmail by Prop(true) { showControlEmail(it) }
        private var isNotEmptyPassword by Prop(true) { showControlPassword(it) }

        override fun bind(data: ProtoViewModelState) {
            data as RegisterViewModel.State
            firstName = data.firstName
            lastName = data.lastName
            email = data.email
            password = data.password
            isEnable = data.isEnable
            isValidFirstName = data.isValidFirstName
            isValidLastName = data.isValidLastName
            isValidEmail = data.isValidEmail
            isNotEmptyFirstName = data.isNotEmptyFirstName
            isNotEmptyLastName = data.isNotEmptyLastName
            isNotEmptyPassword = data.isNotEmptyPassword
        }

        private fun showControlFirstName(it: Boolean, @IdRes msg: Int) {
            fieldErrFirstName.updateProcessValidation(msg, it)
            bv.regTvFirstName.setToggleTextColor(
                R.color.color_text_view,
                R.color.color_error,
                !fieldErrFirstName.hasErrors()
            )
            bv.regBtnRegistration.enable(vm.isValidState())
        }

        private fun showControlLastName(it: Boolean, @IdRes msg: Int) {
            fieldErrLastName.updateProcessValidation(msg, it)
            bv.regTvLastName.setToggleTextColor(
                R.color.color_text_view,
                R.color.color_error,
                !fieldErrLastName.hasErrors()
            )
            bv.regBtnRegistration.enable(vm.isValidState())
        }

        private fun showControlEmail(it: Boolean) {
            bv.regTvEmail.setToggleTextColor(
                R.color.color_text_view,
                R.color.color_error,
                it
            )
            bv.regTvErrEmail.setTextAndVisible(R.string.err_not_valid_email, !it)
            bv.regBtnRegistration.enable(vm.isValidState())
        }

        private fun showControlPassword(it: Boolean) {
            bv.regTvPass.setToggleTextColor(
                R.color.color_text_view,
                R.color.color_error,
                it
            )
            bv.regTvErrPass.setTextAndVisible(R.string.err_empty_field, !it)
            bv.regBtnRegistration.enable(vm.isValidState())
        }
    }
}
