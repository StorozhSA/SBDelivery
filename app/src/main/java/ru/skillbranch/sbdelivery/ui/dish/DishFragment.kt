package ru.skillbranch.sbdelivery.ui.dish

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import ru.skillbranch.sbdelivery.DISH_AMOUNT
import ru.skillbranch.sbdelivery.DISH_ID
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.ui.common.AppTheme
import ru.storozh.models.delivery.database.domains.DishV
import javax.inject.Inject

@InternalCoroutinesApi
@AndroidEntryPoint
class DishFragment : Fragment() {
    @Inject
    lateinit var effectHandler: DishHandler

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.getSerializable(DISH_ID)?.let {

            val initialState = DishFeature.State(
                isFavorite = (it as DishV).favorite,
                isUserAuth = effectHandler.prefs.userIsAuth,
                amount = arguments?.getInt(DISH_AMOUNT) ?: 1
            )

            val feature = DishFeature(
                initialState = initialState,
                effectHandler = effectHandler,
                reducer = DishReducer(),
                scope = lifecycleScope
            )

            return ComposeView(requireContext()).apply {
                setContent {
                    AppTheme {
                        DishContent(feature = feature, dish = it)
                    }
                }
            }
        }
        return inflater.inflate(R.layout.fragment_dish, container, false)
    }
}
