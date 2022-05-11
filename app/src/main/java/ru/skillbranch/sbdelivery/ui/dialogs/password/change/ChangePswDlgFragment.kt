package ru.skillbranch.sbdelivery.ui.dialogs.password.change

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.databinding.FragmentPswChangeBinding
import ru.storozh.common.network.Result
import ru.storozh.delegates.Prop
import ru.storozh.extensions.*
import ru.storozh.ui.ProtoBinding
import ru.storozh.ui.ProtoFragmentDialog
import ru.storozh.ui.ProtoViewModelState
import javax.inject.Inject

@AndroidEntryPoint
@FragmentScoped
class ChangePswDlgFragment @Inject constructor() :
    ProtoFragmentDialog<ChangePswDlgViewModel, ChangePswDlgFragment.Binding>(R.layout.fragment_psw_change) {
    override val vm by viewModels<ChangePswDlgViewModel>()
    public override val bv by viewBinding(FragmentPswChangeBinding::bind)
    override val bs by lazy { Binding() }

    override fun onCreateSetup(view: View, bundle: Bundle?) {
        logd("onCreateSetup invoke")

        // Close dialog
        bv.dlgPswChIvClose.setOnClickListener { dismiss() }

        // Old password
        bv.dlgPswChEtOld.watch {
            vm.state.update { it.copy(passOld = this.toString()) }
            vm.checkState()
        }

        // New password
        bv.dlgPswChEtNew.watch {
            vm.state.update { it.copy(passNew = this.toString()) }
            vm.checkState()
        }

        // Save button
        bv.dlgPswChBtnSave.setOnClickListener {
            if (bv.dlgPswChBtnSave.isEnabled) vm.changePassword()
            lockIFace(true)
            // vm.points.events.mutableAppError().postValue(Event(AppException("E_PASSWORD_CHANGE_FAILED")))
        }

        // Actions in response to a password change
        lifecycleScope.launchWhenStarted {
            vm.points.immutableResChangePasswordFlow().collect {
                lockIFace(false)
                when (it) {
                    is Result.Success.Value -> {
                        logd("Success change password")
                        dismiss()
                    }
                    else -> {
                        logd("Failure change password")
                    }
                }
            }
        }
    }

    private fun lockIFace(lock: Boolean = false) {
        bv.dlgPswChEtOld.isEnabled = !lock
        bv.dlgPswChEtNew.isEnabled = !lock
        bv.dlgPswChBtnSave.isEnabled = !lock
    }

    // Привязка состояния модели
    @SuppressLint("ResourceAsColor")
    inner class Binding : ProtoBinding() {
        private var passOld: String by Prop("") { bv.dlgPswChEtOld.updateText(it) }
        private var passNew: String by Prop("") { bv.dlgPswChEtNew.updateText(it) }
        private var isValidState: Boolean by Prop(true) {
            bv.dlgPswChBtnSave.isEnabled = vm.state.value?.isValidState()!!
        }

        override fun bind(data: ProtoViewModelState) {
            data as ChangePswDlgViewModel.State
            passOld = data.passOld
            passNew = data.passNew
            isValidState = data.isValidState()
        }
    }

    companion object {
        fun newInstance() = ChangePswDlgFragment()
    }
}
