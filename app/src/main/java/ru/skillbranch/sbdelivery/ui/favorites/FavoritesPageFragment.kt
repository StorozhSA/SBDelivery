package ru.skillbranch.sbdelivery.ui.favorites

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.skillbranch.sbdelivery.DISH_ID
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.databinding.FragmentFavoritePageBinding
import ru.skillbranch.sbdelivery.ui.categories.DishesAdapter
import ru.storozh.common.Nav
import ru.storozh.extensions.launchRepeatOnStarted
import ru.storozh.extensions.logd
import ru.storozh.extensions.viewBinding
import ru.storozh.ui.ProtoBinding
import ru.storozh.ui.ProtoFragment
import ru.storozh.ui.ProtoViewModelState
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesPageFragment @Inject constructor() :
    ProtoFragment<FavoritesPageViewModel, FavoritesPageFragment.Binding>(R.layout.fragment_favorite_page) {
    override val vm by viewModels<FavoritesPageViewModel>()
    override val bv by viewBinding(FragmentFavoritePageBinding::bind)
    override val bs by lazy { Binding() }
    private val adapter by lazy {
        DishesAdapter({
            logd("${it.id} is onClick")
            vm.navigate(
                Nav.To(
                    R.id.action_global_nav_dish,
                    bundleOf(DISH_ID to it), null
                )
            )
        }, {
            logd("${it.id} is onClick add button")
        }, {
            logd("${it.id} is onClick like button")
            vm.toggleFavorite(it.id)
        })
    }

    override fun onCreateSetup(view: View, bundle: Bundle?) {
        logd("onViewCreated")
        bv.favoriteRvDishes.adapter = adapter

        // Show name in action bar
        (activity as AppCompatActivity).supportActionBar?.title =
            getString(R.string.label_favorites)

        // Update DishesAdapterPaged
        launchRepeatOnStarted {
            vm.dishes.collectLatest { adapter.submitList(it) }
        }
    }

    // Привязка состояния модели
    inner class Binding : ProtoBinding() {
        override fun bind(data: ProtoViewModelState) {
            data as FavoritesPageViewModel.State
        }
    }
}
