package ru.skillbranch.sbdelivery.ui.recovery

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import dagger.hilt.android.AndroidEntryPoint
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.databinding.FragmentRecoveryBinding
import ru.storozh.common.Nav
import ru.storozh.common.network.Result
import ru.storozh.delegates.Prop
import ru.storozh.extensions.*
import ru.storozh.ui.ProtoBinding
import ru.storozh.ui.ProtoFragment
import ru.storozh.ui.ProtoViewModelState

@AndroidEntryPoint
class RecoveryFragment :
    ProtoFragment<RecoveryViewModel, RecoveryFragment.Binding>(R.layout.fragment_recovery) {
    override val vm by viewModels<RecoveryViewModel>()
    override val bv by viewBinding(FragmentRecoveryBinding::bind)
    override val bs by lazy { Binding() }

    override fun onCreateSetup(view: View, bundle: Bundle?) {
        // E-mail
        bv.recovEtEmail.watch {
            vm.state.update { it.copy(email = this.toString()) }
            vm.checkState()
        }

        // Send button
        bv.recovBtnSend.setOnClickListener {
            hideKeyboard()
            vm.lock()
            vm.recovery()
        }

        // Send result action
        lifecycleScope.launchWhenStarted {
            vm.points.immutableRecovery1PasswordFlow().collect {
                vm.unlock()
                val options = NavOptions.Builder().setPopUpTo(R.id.nav_recovery1, false)
                    .setLaunchSingleTop(true)
                    .build()

                when (it) {
                    is Result.Success -> {
                        logd("Success send recovery email ${it.techCode}")
                        vm.navigate(Nav.To(R.id.action_global_nav_recovery2, null, options))
                    }
                    is Result.Failure -> {
                        logd("Failure send recovery email ${it.techCode}")
                        if (it.techCode == 400) {
                            vm.navigate(Nav.To(R.id.action_global_nav_recovery2, null, options))
                        }
                    }
                }
            }
        }

        vm.state.update { vm.etalon() }
    }

    // Привязка состояния модели
    @SuppressLint("ResourceAsColor")
    inner class Binding : ProtoBinding() {
        private var email by Prop("") { bv.recovEtEmail.updateText(it) }
        private var isEnable by Prop(false) {
            bv.recovBtnSend.enable(it)
            bv.recovEtEmail.enable(it)
        }
        private var isNotEmptyEmail by Prop(false) {
            bv.recovBtnSend.enable(it)
        }

        override fun bind(data: ProtoViewModelState) {
            data as RecoveryViewModel.State
            email = data.email
            isEnable = data.isEnable
            isNotEmptyEmail = data.isNotEmptyEmail
        }
    }
}
