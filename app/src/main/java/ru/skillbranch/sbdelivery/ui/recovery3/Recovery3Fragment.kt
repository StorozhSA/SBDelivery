package ru.skillbranch.sbdelivery.ui.recovery3

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.databinding.FragmentRecovery3Binding
import ru.storozh.common.Nav
import ru.storozh.common.network.Result
import ru.storozh.delegates.Prop
import ru.storozh.extensions.*
import ru.storozh.ui.ProtoBinding
import ru.storozh.ui.ProtoFragment
import ru.storozh.ui.ProtoViewModelState

@AndroidEntryPoint
class Recovery3Fragment :
    ProtoFragment<Recovery3ViewModel, Recovery3Fragment.Binding>(R.layout.fragment_recovery3) {
    override val vm by viewModels<Recovery3ViewModel>()
    override val bv by viewBinding(FragmentRecovery3Binding::bind)
    override val bs by lazy { Binding() }
    private val args: Recovery3FragmentArgs by navArgs()

    override fun onCreateSetup(view: View, bundle: Bundle?) {
        // pass1
        bv.recov3EtPass1.watch {
            vm.state.update { it.copy(pass1 = this.toString()) }
            vm.checkState()
        }
        // pass2
        bv.recov3EtPass2.watch {
            vm.state.update { it.copy(pass2 = this.toString()) }
            vm.checkState()
        }
        // Send button
        bv.recov3BtnSend.setOnClickListener {
            hideKeyboard()
            vm.lock()
            vm.recovery3(args.recoveryCode)
        }

        // Send result action
        lifecycleScope.launchWhenStarted {
            vm.points.immutableRecovery3PasswordFlow().collect {
                vm.unlock()
                val options = NavOptions
                    .Builder()
                    .setPopUpTo(R.id.nav_home, false)
                    .setLaunchSingleTop(true)
                    .build()

                when (it) {
                    is Result.Success -> {
                        logd("Success set new password ${it.techCode}")
                        vm.navigate(Nav.To(R.id.action_global_nav_recovery1, null, options))
                    }
                    else -> logd("Failure set new password ${it.techCode}")
                }
            }
        }
    }

    // Привязка состояния модели
    inner class Binding : ProtoBinding() {
        private var pass1 by Prop("") { bv.recov3EtPass1.updateText(it) }
        private var pass2 by Prop("") { bv.recov3EtPass2.updateText(it) }
        private var isNotEmptyPass1 by Prop(false) { bv.recov3BtnSend.enable(it && vm.isValidState()) }
        private var isNotEmptyPass2 by Prop(false) { bv.recov3BtnSend.enable(it && vm.isValidState()) }
        private var isEqualsPass by Prop(false) { bv.recov3BtnSend.enable(it && vm.isValidState()) }
        private var isEnable by Prop(false) {
            bv.recov3BtnSend.enable(it)
            bv.recov3EtPass1.enable(it)
            bv.recov3EtPass2.enable(it)
        }

        override fun bind(data: ProtoViewModelState) {
            data as Recovery3ViewModel.State
            data.pass1.also { pass1 = it }
            data.pass2.also { pass2 = it }
            data.isEnable.also { isEnable = it }
            data.isNotEmptyPass1.also { isNotEmptyPass1 = it }
            data.isNotEmptyPass2.also { isNotEmptyPass2 = it }
            data.isEqualsPass.also { isEqualsPass = it }
        }
    }
}
