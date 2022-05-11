package ru.skillbranch.sbdelivery.ui.menu

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.skillbranch.sbdelivery.CATEGORY_ID
import ru.skillbranch.sbdelivery.CATEGORY_IS_DISHES
import ru.skillbranch.sbdelivery.CATEGORY_NAME
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.databinding.FragmentMenuBinding
import ru.storozh.common.Nav
import ru.storozh.common.Notify
import ru.storozh.extensions.launchRepeatOnStarted
import ru.storozh.extensions.logd
import ru.storozh.extensions.viewBinding
import ru.storozh.ui.ProtoBinding
import ru.storozh.ui.ProtoFragment
import ru.storozh.ui.ProtoViewModelState

@AndroidEntryPoint
class MenuFragment : ProtoFragment<MenuViewModel, MenuFragment.Binding>(R.layout.fragment_menu) {
    override val vm by viewModels<MenuViewModel>()
    override val bv by viewBinding(FragmentMenuBinding::bind)
    override val bs by lazy { Binding() }
    private val adapterCat by lazy {
        MenuAdapter {
            vm.notify(Notify.TextMessage(it.name))

            if (it.parent == "root" && !it.isDishRoot) {
                vm.navigate(
                    Nav.Direction(
                        MenuFragmentDirections.actionNavMenuToNavCategories(it.id, it.name)
                    )
                )
            } else {
                vm.navigate(
                    Nav.To(
                        R.id.action_global_nav_categories_page,
                        bundleOf(
                            CATEGORY_ID to it.id,
                            CATEGORY_NAME to it.name,
                            CATEGORY_IS_DISHES to true
                        ),
                        null
                    )
                )
            }
        }
    }

    override fun onCreateSetup(view: View, bundle: Bundle?) {
        setHasOptionsMenu(true)
        bv.menuRvCategories.adapter = adapterCat

        // Observe categories
        launchRepeatOnStarted {
            vm.categories().collectLatest {
                adapterCat.submitList(it.sorted())
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_menu_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                logd("Search menu")
                vm.navigate(Nav.Direction(MenuFragmentDirections.actionNavMenuToNavSearch()))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Привязка состояния модели
    inner class Binding : ProtoBinding() {
        override fun bind(data: ProtoViewModelState) {
            data as MenuViewModel.State
        }
    }
}
