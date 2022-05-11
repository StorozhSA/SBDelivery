package ru.skillbranch.sbdelivery.ui.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.databinding.FragmentProfileBinding
import ru.skillbranch.sbdelivery.ui.dialogs.password.change.ChangePswDlgFragment
import ru.skillbranch.sbdelivery.ui.profile.ProfileViewModel.State
import ru.storozh.common.validation.VCTextView
import ru.storozh.delegates.Prop
import ru.storozh.extensions.*
import ru.storozh.ui.ProtoBinding
import ru.storozh.ui.ProtoFragment
import ru.storozh.ui.ProtoViewModelState

@AndroidEntryPoint
class ProfileFragment :
    ProtoFragment<ProfileViewModel, ProfileFragment.Binding>(R.layout.fragment_profile) {
    override val vm by viewModels<ProfileViewModel>()
    override val bv by viewBinding(FragmentProfileBinding::bind)
    override val bs by lazy { Binding() }

    @SuppressLint("RestrictedApi")
    override fun onCreateSetup(view: View, bundle: Bundle?) {
        // Firstname
        bv.profileEtFirstName.watch {
            vm.state.update { it.copy(firstName = this.toString()) }
            vm.checkState()
        }

        // Lastname
        bv.profileEtLastName.watch {
            vm.state.update { it.copy(lastName = this.toString()) }
            vm.checkState()
        }

        // E-mail
        bv.profileEtEmail.watch {
            vm.state.update { it.copy(email = this.toString()) }
            vm.checkState()
        }

        // Button Edit\Save
        bv.profileBtnSave.setOnClickListener {
            hideKeyboard()
            if (vm.state.value!!.isEnable) {
                if (vm.canSave()) {
                    vm.lock()
                    vm.editProfile()
                }
            } else {
                vm.unlock()
            }
        }

        // Button Change password\Cancel
        bv.profileBtnChangel.setOnClickListener {
            hideKeyboard()
            if (vm.state.value!!.isEnable) {
                vm.state.update { vm.etalon() }
            } else {
                ChangePswDlgFragment.newInstance()
                    .show(childFragmentManager, ChangePswDlgFragment.TAG)
            }
        }
    }

    // Привязка состояния модели
    @SuppressLint("ResourceAsColor", "ResourceType")
    inner class Binding : ProtoBinding() {
        private val fieldErrFirstName = VCTextView(bv.profileTvErrFirstName)
        private val fieldErrLastName = VCTextView(bv.profileTvErrLastName)

        private var firstName by Prop("") {
            bv.profileEtFirstName.updateText(it)
            bv.profileBtnSave.enable(vm.canSave())
        }
        private var lastName by Prop("") {
            bv.profileEtLastName.updateText(it)
            bv.profileBtnSave.enable(vm.canSave())
        }
        private var email by Prop("") {
            bv.profileEtEmail.updateText(it)
            bv.profileBtnSave.enable(vm.canSave())
        }
        private var isEnable by Prop(false) {
            bv.profileEtFirstName.enable(it)
            bv.profileEtLastName.enable(it)
            bv.profileEtEmail.enable(it)
            bv.profileBtnSave.enable(!it)
            bv.profileBtnSave.setText(if (it) R.string.labelSave else R.string.labelChange)
            bv.profileBtnChangel.setText(if (it) R.string.labelCancel else R.string.labelChangePassword)
        }
        private var isValidFirstName by Prop(true) {
            showControlFirstName(it, R.string.err_only_letters)
        }
        private var isNotEmptyFirstName by Prop(true) {
            showControlFirstName(it, R.string.err_empty_field)
        }
        private var isValidLastName by Prop(true) {
            showControlLastName(it, R.string.err_only_letters)
        }
        private var isNotEmptyLastName by Prop(true) {
            showControlLastName(it, R.string.err_empty_field)
        }
        private var isValidEmail by Prop(true) { showControlEmail(it) }

        override fun bind(data: ProtoViewModelState) {
            data as State
            firstName = data.firstName
            lastName = data.lastName
            email = data.email
            isEnable = data.isEnable
            isValidFirstName = data.isValidFirstName
            isValidLastName = data.isValidLastName
            isValidEmail = data.isValidEmail
            isNotEmptyFirstName = data.isNotEmptyFirstName
            isNotEmptyLastName = data.isNotEmptyLastName
        }

        private fun showControlFirstName(it: Boolean, @IdRes msg: Int) {
            fieldErrFirstName.updateProcessValidation(msg, it)
            bv.profileTvFirstName.setToggleTextColor(
                R.color.color_text_view,
                R.color.color_error,
                !fieldErrFirstName.hasErrors()
            )
            bv.profileBtnSave.enable(vm.canSave())
        }

        private fun showControlLastName(it: Boolean, @IdRes msg: Int) {
            fieldErrLastName.updateProcessValidation(msg, it)
            bv.profileTvLastName.setToggleTextColor(
                R.color.color_text_view,
                R.color.color_error,
                !fieldErrLastName.hasErrors()
            )
            bv.profileBtnSave.enable(vm.canSave())
        }

        private fun showControlEmail(it: Boolean) {
            bv.profileTvEmail.setToggleTextColor(
                R.color.color_text_view,
                R.color.color_error,
                it
            )
            bv.profileTvErrEmail.setTextAndVisible(R.string.err_not_valid_email, !it)
            bv.profileBtnSave.enable(vm.canSave())
        }
    }
}
