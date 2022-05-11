package ru.skillbranch.sbdelivery.ui.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.skillbranch.sbdelivery.DISH_ID
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.databinding.FragmentHomeBinding
import ru.skillbranch.sbdelivery.ui.categories.DishesAdapter
import ru.skillbranch.sbdelivery.ui.home.HomeViewModel.State
import ru.storozh.common.Nav
import ru.storozh.extensions.*
import ru.storozh.models.delivery.database.domains.CartItem
import ru.storozh.models.delivery.database.domains.DishV
import ru.storozh.ui.ProtoBinding
import ru.storozh.ui.ProtoFragment
import ru.storozh.ui.ProtoViewModelState

@AndroidEntryPoint
class HomeFragment : ProtoFragment<HomeViewModel, HomeFragment.Binding>(R.layout.fragment_home) {
    override val vm by viewModels<HomeViewModel>()
    override val bv by viewBinding(FragmentHomeBinding::bind)
    override val bs by lazy { Binding() }
    private val adapterDishesR by lazy {
        DishesAdapter({
            logd("${it.id} is onClick")
            goToDish(it)
        }, {
            vm.database.saveCart(listOf(CartItem(id = it.id, amount = 1, price = it.price)))
        }, { vm.toggleFavorite(it.id) })
    }
    private val adapterDishesB by lazy {
        DishesAdapter({
            logd("${it.id} is onClick")
            goToDish(it)
        }, {
            vm.database.saveCart(listOf(CartItem(id = it.id, amount = 1, price = it.price)))
        }, { vm.toggleFavorite(it.id) })
    }
    private val adapterDishesP by lazy {
        DishesAdapter({
            logd("${it.id} is onClick")
            goToDish(it)
        }, {
            vm.database.saveCart(listOf(CartItem(id = it.id, amount = 1, price = it.price)))
        }, { vm.toggleFavorite(it.id) })
    }

    private fun goToDish(dish: DishV) {
        vm.navigate(
            Nav.To(
                R.id.action_global_nav_dish,
                bundleOf(DISH_ID to dish), null
            )
        )
    }

    override fun onCreateSetup(view: View, bundle: Bundle?) {
        setHasOptionsMenu(true)

        bv.homeRvRecommended.layoutManager = buildGLM()
        bv.homeRvRecommended.adapter = adapterDishesR

        bv.homeRvBest.layoutManager = buildGLM()
        bv.homeRvBest.adapter = adapterDishesB

        bv.homeRvPopular.layoutManager = buildGLM()
        bv.homeRvPopular.adapter = adapterDishesP

        launchRepeatOnStarted {
            vm.getAllRecommendationDishes().collectLatest {
                logd("getAllRecommendationDishes -> $it")
                if (it.isEmpty()) {
                    bv.homeClRecommended.gone()
                } else {
                    adapterDishesR.submitList(it.shuffled())
                    bv.homeClRecommended.visible(true)
                }
            }
        }

        launchRepeatOnStarted {
            vm.getAllBestDishes(4.8).collectLatest {
                logd("getAllBestDishes -> $it")
                if (it.size >= 4) {
                    adapterDishesB.submitList(it.shuffled())
                    bv.homeClBest.visible(true)
                } else {
                    bv.homeClBest.gone()
                }
            }
        }

        launchRepeatOnStarted {
            vm.getAllPopularDishes().collectLatest {
                logd("getAllPopularDishes -> $it")
                adapterDishesP.submitList(it)
            }
        }

        bv.homeTvRecommendedLook.setOnClickListener {
            vm.navigate(Nav.Direction(HomeFragmentDirections.actionGlobalNavMenu()))
        }
        bv.homeTvBestLook.setOnClickListener {
            vm.navigate(Nav.Direction(HomeFragmentDirections.actionGlobalNavMenu()))
        }
        bv.homeTvPopularLook.setOnClickListener {
            vm.navigate(Nav.Direction(HomeFragmentDirections.actionGlobalNavMenu()))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_shop_cart -> {
                logd("Home menu")
                // vm.navigate(Nav.Direction(MenuFragmentDirections.actionNavMenuToNavSearch()))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupBadge() {
    }

    private fun buildGLM() = GridLayoutManager(context, 1).apply {
        orientation = RecyclerView.HORIZONTAL
    }

    // Привязка состояния модели
    inner class Binding : ProtoBinding() {
        override fun bind(data: ProtoViewModelState) {
            data as State
        }
    }
}
