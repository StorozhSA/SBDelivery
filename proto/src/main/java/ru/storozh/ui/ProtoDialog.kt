package ru.storozh.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.viewbinding.ViewBinding
import ru.storozh.R
import ru.storozh.common.Nav
import ru.storozh.extensions.logd
import ru.storozh.extensions.setWidthPercent

abstract class ProtoDialog(@LayoutRes private val contentLayoutId: Int) : DialogFragment() {
    protected abstract val bv: ViewBinding
    lateinit var nc: NavController
    protected abstract fun onCreateSetup(view: View, bundle: Bundle?)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logd("onCreateView invoke")
        return inflater.inflate(contentLayoutId, container, false)
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        logd("onViewCreated invoke")
        super.onViewCreated(view, bundle)
        onCreateSetup(view, bundle)
        nc = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment) //default
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        setWidthPercent(90)
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
            is Nav.PopBackStack -> nc.popBackStack()
        }
    }
}