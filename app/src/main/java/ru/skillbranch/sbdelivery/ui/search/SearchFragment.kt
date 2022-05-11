package ru.skillbranch.sbdelivery.ui.search

import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.skillbranch.sbdelivery.*
import ru.skillbranch.sbdelivery.databinding.FragmentSearchBinding
import ru.skillbranch.sbdelivery.ui.categories.DishesAdapter
import ru.skillbranch.sbdelivery.ui.menu.MenuAdapter
import ru.storozh.common.Nav
import ru.storozh.common.Notify
import ru.storozh.delegates.Prop
import ru.storozh.extensions.*
import ru.storozh.ui.ProtoBinding
import ru.storozh.ui.ProtoFragment
import ru.storozh.ui.ProtoViewModelState

@AndroidEntryPoint
class SearchFragment :
    ProtoFragment<SearchViewModel, SearchFragment.Binding>(R.layout.fragment_search) {
    override val vm by viewModels<SearchViewModel>()
    override val bv by viewBinding(FragmentSearchBinding::bind)
    override val bs by lazy { Binding() }
    private lateinit var lm: GridLayoutManager
    private lateinit var sw: SearchView
    private val adapterSearch by lazy {
        SearchAdapter(
            {
                logd("SearchAdapter.listenerOnClickText -> $it")
                sw.setQuery(it, false)
                context?.hideKeyboard(this@SearchFragment.requireView())
                // vm.state.update { state -> state.copy(searchText = it) }
            },
            {
                logd("SearchAdapter.listenerOnClickClose -> $it")
                vm.searchHistoryItemDelete(it)
            }
        )
    }
    private val adapterCat by lazy {
        MenuAdapter {
            vm.searchHistoryItemAdd(sw.query.toString())
            vm.notify(Notify.TextMessage("GO TO ${it.name}"))
            if (it.parent == "root" && !it.isDishRoot) {
                vm.navigate(
                    Nav.Direction(
                        SearchFragmentDirections.actionGlobalNavCategories(
                            it.id,
                            it.name
                        )
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
    private val adapterDishes by lazy {
        DishesAdapter(
            {
                logd("${it.id} is onClick")
                // Add to history
                vm.searchHistoryItemAdd(sw.query.toString())

                // Go to dish detail
                vm.navigate(
                    Nav.To(
                        R.id.action_global_nav_dish,
                        bundleOf(DISH_ID to it), null
                    )
                )
            },
            {},
            {
                logd("${it.id} is onClick like button")
                vm.toggleFavorite(it.id)
            }
        )
    }
    private val adapterConcat by lazy { ConcatAdapter(adapterSearch) }

    override fun onCreateSetup(view: View, bundle: Bundle?) {
        lm = GridLayoutManager(context, 2)
        setHasOptionsMenu(true)
        bv.searchRvHistory.layoutManager = lm
        bv.searchRvHistory.adapter = adapterConcat

        launchRepeatOnStarted {
            vm.searchHistory.collectLatest {
                vm.state.update { state -> state.copy(searchHistory = it) }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        logd("invoke onCreateOptionsMenu")
        inflater.inflate(R.menu.fragment_search_menu, menu)
        sw = menu.findItem(R.id.x_search).actionView as SearchView
        sw.isIconified = false
        sw.queryHint = getString(R.string.label_search)
        sw.setQuery(vm.state.value?.searchText ?: "", false)
        sw.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(query: String): Boolean {
                logd("onQueryTextChange -> $query")
                vm.state.update { it.copy(searchText = query) }
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                logd("onQueryTextSubmit -> $query")
                // vm.state.update { it.copy(searchText = query) }
                return false
            }
        })
    }

    override fun onDestroy() {
        logd("invoke onDestroy")
        super.onDestroy()
        sw.setOnQueryTextListener(null)
    }

    // Привязка состояния модели
    inner class Binding : ProtoBinding() {
        private var searchText by Prop(vm.state.value?.searchText!!) {
            if (it.isNotBlank()) {
                // Clean previous results
                adapterCat.submitList(emptyList())
                adapterDishes.submitList(emptyList())

                // Find categories
                launchRepeatOnStarted {
                    vm.searchCategory(it).collectLatest { list ->
                        logd("Find cat -> $list")
                        if (list.isNotEmpty()) {
                            adapterConcat.removeAdapter(adapterSearch)
                            adapterCat.submitList(list)
                            adapterConcat.addAdapter(adapterCat)
                            orientation()
                        }
                    }
                }

                // Find dishes
                launchRepeatOnStarted {
                    vm.searchDishes(it).collectLatest { list ->
                        logd("Find dishes -> $list")
                        if (list.isNotEmpty()) {
                            adapterConcat.removeAdapter(adapterSearch)
                            adapterDishes.submitList(list)
                            adapterConcat.addAdapter(adapterDishes)
                            orientation()
                        }
                    }
                }
            } else {
                logd("searchText empty")
                showSearchHistory()
            }
        }

        private var searchHistory: List<String> by Prop(vm.state.value?.searchHistory!!) {
            adapterSearch.submitList(it)
        }

        override fun bind(data: ProtoViewModelState) {
            data as SearchViewModel.State
            searchText = data.searchText
            searchHistory = data.searchHistory
        }
    }

    private fun orientation() {
        when (activity?.resources?.configuration?.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> lm.spanCount = 4
            else -> lm.spanCount = 2
        }
    }

    private fun showSearchHistory() {
        adapterConcat.removeAdapter(adapterCat)
        adapterConcat.removeAdapter(adapterDishes)
        adapterConcat.addAdapter(adapterSearch)
        lm.spanCount = 1
    }
}
