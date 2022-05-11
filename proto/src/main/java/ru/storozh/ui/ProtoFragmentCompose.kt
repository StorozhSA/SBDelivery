package ru.storozh.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.hilt.lifecycle.ViewModelAssistedFactory
import androidx.navigation.NavController
import androidx.navigation.Navigation
import ru.storozh.R
import ru.storozh.common.Nav
import ru.storozh.extensions.logd


abstract class ProtoFragmentCompose<T : ProtoViewModelCompose<out ProtoViewModelState>> :
    Fragment() {
    protected var viewModelFactory: ViewModelAssistedFactory<T>? = null
    protected abstract val vm: T
    lateinit var nc: NavController
    protected abstract fun onCreateSetup(view: View, bundle: Bundle?)

    override fun onViewCreated(view: View, bundle: Bundle?) {
        logd("onViewCreated invoke")
        super.onViewCreated(view, bundle)
        nc = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment) //default
        onCreateSetup(view, bundle)
        vm.observeNavigation(viewLifecycleOwner) { navigateByCommand(it) }
    }

    private fun navigateByCommand(command: Nav) {
        when (command) {
            is Nav.To -> {
                nc.navigate(
                    command.destination,
                    command.args,
                    command.options,
                    command.extras
                )
            }
            is Nav.Direction -> {
                nc.navigate(
                    command.directions
                )
            }
            is Nav.PopBackStack -> {
                if (command.destination == 0) {
                    nc.popBackStack()
                } else {
                    nc.popBackStack(command.destination, command.inclusive)
                }
            }
        }
    }
}