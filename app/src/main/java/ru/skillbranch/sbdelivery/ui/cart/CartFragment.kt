package ru.skillbranch.sbdelivery.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.ui.cart.logic.CartFeature
import ru.skillbranch.sbdelivery.ui.cart.logic.CartHandler
import ru.skillbranch.sbdelivery.ui.cart.logic.CartReducer
import ru.skillbranch.sbdelivery.ui.common.AppTheme
import ru.storozh.extensions.update
import javax.inject.Inject

@InternalCoroutinesApi
@AndroidEntryPoint
class CartFragment : Fragment() {
    var cartSize = 3

    @Inject
    lateinit var effectHandler: CartHandler

    private val initialState by lazy {
        CartFeature.State(
            isUserAuth = effectHandler.prefs.userIsAuth
        )
    }

    private val feature by lazy {
        CartFeature(
            initialState = initialState,
            effectHandler = effectHandler.apply { setNavController(this@CartFragment.findNavController()) },
            reducer = CartReducer(),
            scope = lifecycleScope
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            effectHandler.points.immutableCartItemJoinedFlow().onEach {
                // feature.state.update { state -> state.copy(list = CartFeature.StateUi.Loading) }
                // delay(2000)
            }.collectLatest {
                feature.state.update { state ->
                    state.copy(
                        list = if (it.isEmpty()) {
                            CartFeature.StateUi.Empty
                        } else {
                            CartFeature.StateUi.Value(it)
                        }
                    )
                }
            }
        }
        effectHandler.database.getCart(scope = lifecycleScope)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return try {
            ComposeView(requireContext()).apply {
                setContent {
                    AppTheme {
                        CartContent(feature)
                    }
                }
            }
        } catch (e: Throwable) {
            inflater.inflate(R.layout.fragment_cart, container, false)
        }
    }
}
