package ru.skillbranch.sbdelivery.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.databinding.ActivityMainBinding
import ru.storozh.common.Nav
import ru.storozh.common.network.Result.Success.Value
import ru.storozh.delegates.Prop
import ru.storozh.extensions.*
import ru.storozh.ui.ProtoActivity
import ru.storozh.ui.ProtoBinding
import ru.storozh.ui.ProtoViewModelState

@SuppressLint("RestrictedApi")
@AndroidEntryPoint
class MainActivity : ProtoActivity<MainViewModel, MainActivity.Binding>() {
    override val vm by viewModels<MainViewModel>()
    override val bv by viewBinding(ActivityMainBinding::inflate)
    override val bs by lazy { Binding() }
    private val navHost by lazy { supportFragmentManager.findFragmentById(R.id.nav_host_fragment) }
    private val navHeader by lazy { bv.navView.getHeaderView(0) }
    private val imgLogOut by lazy { navHeader.findViewById<ImageView>(R.id.iv_nav_logout) }
    private val tvEmail by lazy { navHeader.findViewById<TextView>(R.id.tv_nav_header_email) }
    private val tvName by lazy { navHeader.findViewById<TextView>(R.id.tv_nav_header_name) }
    private val navListener by lazy {
        NavController.OnDestinationChangedListener { _, dest, _ ->
            logd("Try go to ${dest.displayName}")
        }
    }
    private val logOutListener by lazy {
        View.OnClickListener { vm.prefs.userIsAuth = false }
    }
    private val appBarConfiguration: AppBarConfiguration by lazy {
        AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_menu,
                R.id.nav_favorites,
                R.id.nav_cart,
                R.id.nav_profile,
                // R.id.nav_orders,
                // R.id.nav_notifications,
                R.id.nav_about
            ),
            bv.drawerLayout
        )
    }

    override fun onCreateSetup(savedInstanceState: Bundle?) {
        nc = (navHost as NavHostFragment).findNavController()
        setSupportActionBar(bv.appBar.toolbar)
        setupActionBarWithNavController(nc, appBarConfiguration)
        bv.navView.setupWithNavController(nc)

        // Show\Hide ProgressBar
        launchRepeatOnStarted {
            vm.points.iLoad().collect {
                if (it && !bv.progressBar.isVisible) {
                    bv.progressBar.visible(true)
                    delay(2000)
                    bv.progressBar.visible(false)
                }
            }
        }

        // Update Header by Register data
        launchRepeatOnStarted {
            vm.points.immutableResRegisterFlow().collect {
                if (it is Value) {
                    vm.state.update { state ->
                        state.copy(
                            name = "${it.payload.firstName} ${it.payload.lastName}",
                            email = it.payload.email,
                            isAuth = true,
                            isRegister = true
                        )
                    }
                }
            }
        }

        // Update Header by Login data
        launchRepeatOnStarted {
            vm.points.immutableResLoginFlow().collect {
                logd("Invoke observer points.immutableResLogin() in MainActivity")
                if (it is Value) {
                    vm.state.update { state ->
                        state.copy(
                            name = "${it.payload.firstName} ${it.payload.lastName}",
                            email = it.payload.email,
                            isAuth = true
                        )
                    }
                }
            }
        }

        // Update Header by Edit profile new data
        launchRepeatOnStarted {
            vm.points.immutableResUserProfileFlow().collect {
                if (it is Value) {
                    vm.state.update { state ->
                        state.copy(
                            name = "${it.payload.firstName} ${it.payload.lastName}",
                            email = it.payload.email
                        )
                    }
                }
            }
        }

        // About
        bv.btnAbout.setOnClickListener {
            logd("btnAbout invoke click listener")
            vm.navigate(Nav.To(R.id.action_global_nav_about))
            bv.drawerLayout.closeDrawers()
        }

        // Press logout image action
        imgLogOut.setOnClickListener(logOutListener)

        // Secure intercept navigation
        nc.addOnDestinationChangedListener(navListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        nc.removeOnDestinationChangedListener(navListener)
        imgLogOut.setOnClickListener(null)
        bv.btnAbout.setOnClickListener(null)
    }

    override fun onSupportNavigateUp(): Boolean {
        return nc.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    // Привязка состояния модели
    inner class Binding : ProtoBinding() {
        private var name: String by Prop("") { tvName.text = it }
        private var email: String by Prop("") { tvEmail.text = it }
        private var isAuth: Boolean by Prop(false) {
            imgLogOut.visible(it)
            tvEmail.visible(it)
            tvName.visible(it)
        }

        override fun bind(data: ProtoViewModelState) {
            data as MainViewModel.State
            data.also {
                name = it.name
                email = it.email
                isAuth = it.isAuth
            }
        }
    }
}
