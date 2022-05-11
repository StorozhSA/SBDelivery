package ru.skillbranch.sbdelivery.ui.recovery2

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.databinding.FragmentRecovery2Binding
import ru.storozh.common.Nav
import ru.storozh.common.network.Result
import ru.storozh.delegates.Prop
import ru.storozh.extensions.*
import ru.storozh.ui.ProtoBinding
import ru.storozh.ui.ProtoFragment
import ru.storozh.ui.ProtoViewModelState

@AndroidEntryPoint
class Recovery2Fragment :
    ProtoFragment<Recovery2ViewModel, Recovery2Fragment.Binding>(R.layout.fragment_recovery2) {
    override val vm by viewModels<Recovery2ViewModel>()
    override val bv by viewBinding(FragmentRecovery2Binding::bind)
    override val bs by lazy { Binding() }

    override fun onCreateSetup(view: View, bundle: Bundle?) {
        // d1
        bv.recov2EtD1.watch {
            vm.state.update { it.copy(d1 = this.toString()) }
            vm.checkState()
        }
        // d2
        bv.recov2EtD2.watch {
            vm.state.update { it.copy(d2 = this.toString()) }
            vm.checkState()
        }
        // d3
        bv.recov2EtD3.watch {
            vm.state.update { it.copy(d3 = this.toString()) }
            vm.checkState()
        }
        // d4
        bv.recov2EtD4.watch {
            vm.state.update { it.copy(d4 = this.toString()) }
            vm.checkState()
        }

        // Send result action
        lifecycleScope.launchWhenStarted {
            vm.points.immutableRecovery2PasswordFlow().collect {
                vm.unlock()
                when (it) {
                    is Result.Success.Value -> {
                        logd("Success send recovery code")
                        vm.state.value?.let { it1 ->
                            logd("Go to Recovery3Fragment")
                            vm.navigate(
                                Nav.Direction(
                                    Recovery2FragmentDirections.actionNavRecovery2ToNavRecovery3(
                                        it1.code()
                                    )
                                )
                            )
                        }
                    }
                    else -> logd("Failure send recovery code")
                }
            }
        }
    }

    // Привязка состояния модели
    @SuppressLint("ResourceAsColor")
    inner class Binding : ProtoBinding() {
        private var d1 by Prop("") { bv.recov2EtD1.updateText(it) }
        private var d2 by Prop("") { bv.recov2EtD2.updateText(it) }
        private var d3 by Prop("") { bv.recov2EtD3.updateText(it) }
        private var d4 by Prop("") { bv.recov2EtD4.updateText(it) }
        private var isEnable by Prop(false) {
            bv.recov2EtD1.enable(it)
            bv.recov2EtD2.enable(it)
            bv.recov2EtD3.enable(it)
            bv.recov2EtD4.enable(it)
        }
        private var isEnableD1 by Prop(false) {
            if (it) {
                bv.recov2EtD2.enable(it)
                bv.recov2EtD2.requestFocus()
            }
        }
        private var isEnableD2 by Prop(false) {
            if (it) {
                bv.recov2EtD3.enable(it)
                bv.recov2EtD3.requestFocus()
            }
        }
        private var isEnableD3 by Prop(false) {
            if (it) {
                bv.recov2EtD4.enable(it)
                bv.recov2EtD4.requestFocus()
            }
        }
        private var isEnableD4 by Prop(false) {}
        private var isNotEmpty by Prop(false) {
            if (it) {
                hideKeyboard()
                vm.lock()
                vm.recovery2()
            }
        }

        override fun bind(data: ProtoViewModelState) {
            data as Recovery2ViewModel.State
            d1 = data.d1
            d2 = data.d2
            d3 = data.d3
            d4 = data.d4
            isEnable = data.isEnable
            isNotEmpty = data.isNotEmpty
            isEnableD1 = data.isEnableD1
            isEnableD2 = data.isEnableD2
            isEnableD3 = data.isEnableD3
            isEnableD4 = data.isEnableD4
        }
    }
}
