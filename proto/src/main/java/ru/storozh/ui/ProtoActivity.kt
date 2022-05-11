package ru.storozh.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.hilt.lifecycle.ViewModelAssistedFactory
import androidx.navigation.NavController
import androidx.viewbinding.ViewBinding
import ru.storozh.common.Nav
import ru.storozh.extensions.logd
import javax.inject.Inject

abstract class ProtoActivity<T : ProtoViewModel<out ProtoViewModelState>, S : ProtoBinding> :
    AppCompatActivity() {
    protected var viewModelFactory: ViewModelAssistedFactory<T>? = null
    abstract val vm: T
    protected abstract val bs: S
    protected abstract val bv: ViewBinding
    lateinit var nc: NavController
    protected abstract fun onCreateSetup(savedInstanceState: Bundle?)

    @Inject
    fun postConstruct() {
        logd("invoke postConstruct() method")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        logd("onCreate invoke")
        super.onCreate(savedInstanceState)
        setContentView(bv.root) // Binding XML Markup to an Activity
        onCreateSetup(savedInstanceState) // Additional initialization in a child Activity
        vm.state.observe(this) { bs.bind(it) } // Linking model state with Activity bindingState
        vm.observeNavigation(this) { navigateByCommand(it) }
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