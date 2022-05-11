package ru.skillbranch.sbdelivery.ui.categories

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.MODE_SCROLLABLE
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ru.skillbranch.sbdelivery.*
import ru.skillbranch.sbdelivery.databinding.FragmentCategoryBinding
import ru.storozh.delegates.Prop
import ru.storozh.extensions.logd
import ru.storozh.extensions.update
import ru.storozh.extensions.viewBinding
import ru.storozh.models.delivery.database.domains.Category
import ru.storozh.ui.ProtoBinding
import ru.storozh.ui.ProtoFragment
import ru.storozh.ui.ProtoViewModelState

@AndroidEntryPoint
class CategoriesFragment : ProtoFragment<CategoriesViewModel, CategoriesFragment.Binding>
    (R.layout.fragment_category) {
    override val vm by activityViewModels<CategoriesViewModel>()
    override val bv by viewBinding(FragmentCategoryBinding::bind)
    override val bs by lazy { Binding() }
    private val args: CategoriesFragmentArgs by navArgs()
    private lateinit var tabs: TabLayout
    private lateinit var vp2: ViewPager2

    override fun onCreateSetup(view: View, bundle: Bundle?) {
        logd("invoke onViewCreated")
        setHasOptionsMenu(true)

        // Parent category name in actionbar
        (activity as AppCompatActivity).supportActionBar?.title = args.parentCategoryName

        vm.state.value?.let {

            // Init viewpager2
            vp2 = bv.menuCatViewPager.apply {
                setPageTransformer(DepthPageTransformer())
                offscreenPageLimit = 1
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        vm.state.update { state -> state.copy(currentTab = position) }
                    }
                })
            }

            // Init tabs
            tabs = bv.menuCatTabs.apply { tabMode = MODE_SCROLLABLE }

            showCategories(it.categories)
        }
    }

    override fun onStart() {
        super.onStart()
        // Load categories
        vm.loadCats(args.parentCategoryId)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_category_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_dishes -> {
                logd("Select menu")

                true
            }
            R.id.sort_alphabet -> {
                logd("Select menu sort by alphabet")
                vm.state.update {
                    it.copy(sortByMode = SORT_BY_ALPHABET, sortByDirect = !it.sortByDirect)
                }
                true
            }
            R.id.sort_popular -> {
                logd("Select menu sort by popular")
                vm.state.update {
                    it.copy(sortByMode = SORT_BY_POPULAR, sortByDirect = !it.sortByDirect)
                }
                true
            }
            R.id.sort_rating -> {
                logd("Select menu sort by rating")
                vm.state.update {
                    it.copy(sortByMode = SORT_BY_RATING, sortByDirect = !it.sortByDirect)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showCategories(cats: List<Category>) {
        // Загружаем список подкатегорий
        vp2.adapter = Adapter(cats, this@CategoriesFragment)
        // Настройка режима отображения табов в зависимости от длины имен категорий
        // if (cats.sumOf { cat -> cat.name.length } < 30) tabs.tabMode = MODE_FIXED
        // Синхронизируем табы и viewpager
        TabLayoutMediator(tabs, vp2) { tab, pos -> tab.text = cats[pos].name }.attach()
    }

    // Привязка состояния модели
    inner class Binding : ProtoBinding() {
        var categories: List<Category> by Prop(emptyList()) { showCategories(it) }
        private var currentTab: Int by Prop(-1) { vp2.currentItem = it }

        override fun bind(data: ProtoViewModelState) {
            data as CategoriesViewModel.State
            categories = data.categories
            currentTab = data.currentTab
        }
    }

    // Адаптер для ViewPager2
    class Adapter(private val cats: List<Category>, ownFrag: Fragment) :
        FragmentStateAdapter(ownFrag) {
        override fun getItemCount(): Int = cats.size

        override fun createFragment(position: Int): Fragment {
            return CategoriesPageFragment().apply {
                arguments = bundleOf(CATEGORY_ID to cats[position].id)
                logd("Created a fragment for category = ${cats[position].id} and position $position")
            }
        }
    }
}
