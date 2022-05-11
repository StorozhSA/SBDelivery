package ru.skillbranch.sbdelivery.ui.categories

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.skillbranch.sbdelivery.*
import ru.skillbranch.sbdelivery.databinding.FragmentCategoryPageBinding
import ru.storozh.common.Nav
import ru.storozh.extensions.launchRepeatOnStarted
import ru.storozh.extensions.logd
import ru.storozh.extensions.update
import ru.storozh.extensions.viewBinding
import ru.storozh.ui.ProtoBinding
import ru.storozh.ui.ProtoFragment
import ru.storozh.ui.ProtoViewModelState
import javax.inject.Inject

@AndroidEntryPoint
class CategoriesPageFragment @Inject constructor() :
    ProtoFragment<CategoriesPageViewModel, CategoriesPageFragment.Binding>(R.layout.fragment_category_page) {
    override val vm by viewModels<CategoriesPageViewModel>()
    private val svm by activityViewModels<CategoriesViewModel>()
    override val bv by viewBinding(FragmentCategoryPageBinding::bind)
    override val bs by lazy { Binding() }
    private val adapter by lazy {
        DishesAdapterPaged(
            {
                logd("${it.id} is onClick")
                vm.navigate(
                    Nav.To(
                        R.id.action_global_nav_dish,
                        bundleOf(DISH_ID to it), null
                    )
                )
            },
            {
                logd("${it.id} is onClick add button")
            },
            {
                logd("${it.id} is onClick like button")
                vm.toggleFavorite(it.id)
            }
        )
    }

    override fun onCreateSetup(view: View, bundle: Bundle?) {
        logd("onViewCreated")
        bv.catRvDishes.adapter = adapter

        // ON/OFF menu edit, if CATEGORY_IS_DISHES
        arguments?.getBoolean(CATEGORY_IS_DISHES)?.let { setHasOptionsMenu(it) }

        // Show name in action bar
        arguments?.getString(CATEGORY_NAME)?.let {
            (activity as AppCompatActivity).supportActionBar?.title = it
        }

        // Update local state, if categoryId not set
        vm.state.value?.run {
            if (categoryId.isBlank()) {
                arguments?.getString(CATEGORY_ID)?.let { categoryId ->
                    logd("Set category id = $categoryId")
                    vm.state.update { it.copy(categoryId = categoryId) }
                }
            }
        }

        // Update local state from shared state, if change sort order and/or sorting direct
        svm.state.observe(viewLifecycleOwner, { sharedState ->
            if (sharedState.sortByMode != vm.state.value?.sortByMode ||
                sharedState.sortByDirect != vm.state.value?.sortByDirect
            ) {
                vm.state.update {
                    it.copy(
                        sortByMode = sharedState.sortByMode,
                        sortByDirect = sharedState.sortByDirect
                    )
                }
            }
        })

        // Update DishesAdapterPaged
        launchRepeatOnStarted {
            vm.dishes.onEach {
                // Cancel refreshing
                launch {
                    delay(1000)
                    bv.catPageSwipeRefreshLayout.isRefreshing = false
                }
            }.collectLatest { adapter.submitData(it) }
        }

        // Update data if activate manual refresh
        bv.catPageSwipeRefreshLayout.setOnRefreshListener {
            logd("Manual refresh catPageSwipeRefreshLayout")
            vm.state.update { it.copy(manualUpdate = !it.manualUpdate) }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (arguments?.getBoolean(CATEGORY_IS_DISHES) == true) {
            inflater.inflate(R.menu.fragment_category_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (arguments?.getBoolean(CATEGORY_IS_DISHES) == true) {
            return when (item.itemId) {
                R.id.action_sort_dishes -> {
                    logd("Select menu")
                    true
                }
                R.id.sort_alphabet -> {
                    logd("Select menu sort by alphabet")
                    svm.state.update {
                        it.copy(sortByMode = SORT_BY_ALPHABET, sortByDirect = !it.sortByDirect)
                    }
                    true
                }
                R.id.sort_popular -> {
                    logd("Select menu sort by popular")
                    svm.state.update {
                        it.copy(sortByMode = SORT_BY_POPULAR, sortByDirect = !it.sortByDirect)
                    }
                    true
                }
                R.id.sort_rating -> {
                    logd("Select menu sort by rating")
                    svm.state.update {
                        it.copy(sortByMode = SORT_BY_RATING, sortByDirect = !it.sortByDirect)
                    }
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Привязка состояния модели
    inner class Binding : ProtoBinding() {
        override fun bind(data: ProtoViewModelState) {
            data as CategoriesPageViewModel.State
        }
    }
}
