package ru.storozh.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.hilt.lifecycle.ViewModelAssistedFactory
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.viewbinding.ViewBinding
import ru.storozh.R
import ru.storozh.common.Nav


abstract class ProtoFragment<T : ProtoViewModel<out ProtoViewModelState>, S : ProtoBinding>(@LayoutRes contentLayoutId: Int) :
    Fragment(contentLayoutId) {
    protected var viewModelFactory: ViewModelAssistedFactory<T>? = null
    protected abstract val vm: T
    protected abstract val bs: S
    protected abstract val bv: ViewBinding
    lateinit var nc: NavController
    protected abstract fun onCreateSetup(view: View, bundle: Bundle?)


    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        nc = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment) //default
        onCreateSetup(view, bundle)
        vm.observeNavigation(viewLifecycleOwner) { navigateByCommand(it) }
        vm.state.observe(viewLifecycleOwner) { bs.bind(it) } // Linking model state with Activity bindingState
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