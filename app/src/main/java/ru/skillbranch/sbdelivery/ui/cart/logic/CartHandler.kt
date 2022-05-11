package ru.skillbranch.sbdelivery.ui.cart.logic

import androidx.core.os.bundleOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import ru.skillbranch.sbdelivery.AppSharedPreferencesHelper
import ru.skillbranch.sbdelivery.DISH_AMOUNT
import ru.skillbranch.sbdelivery.DISH_ID
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.data.EndPointsImpl
import ru.skillbranch.sbdelivery.data.RepoDataBase
import ru.skillbranch.sbdelivery.data.RepoNetwork
import ru.skillbranch.sbdelivery.ui.cart.logic.CartFeature.Eff
import ru.skillbranch.sbdelivery.ui.cart.logic.CartFeature.Msg
import ru.storozh.common.AppSharedPreferences
import ru.storozh.common.EndPoints
import ru.storozh.common.Nav
import ru.storozh.forcompose.ProtoHandler
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@InternalCoroutinesApi
class CartHandler @Inject constructor(
    val database: RepoDataBase,
    val network: RepoNetwork,
    private val point: EndPoints,
    private val pref: AppSharedPreferences
) : ProtoHandler<Eff, Msg>() {
    val points by lazy { point as EndPointsImpl }
    val prefs by lazy { pref as AppSharedPreferencesHelper }

    override suspend fun handle(effect: Eff) {
        when (effect) {
            // Increment
            is Eff.IncrementItem -> withContext(coroutineContext + parentJob) {
                database.incrementCartItem(effect.dishId)
                delay(700)
                commit(Msg.IncrementComplete(effect.dishId))
            }

            // Decrement
            is Eff.DecrementItem -> withContext(coroutineContext + parentJob) {
                database.decrementCartItem(effect.dishId)
                delay(700)
                commit(Msg.DecrementComplete(effect.dishId))
            }

            // Remove
            is Eff.RemoveItem -> withContext(coroutineContext + parentJob) {
                database.removeCartItem(effect.dishId)
                commit(Msg.RemoveFromCartComplete(effect.dishId))
            }

            // Send order
            is Eff.SendOrder -> withContext(coroutineContext + parentJob) {
                // todo
                commit(Msg.SendOrderComplete)
            }

            // Click on dish
            is Eff.ClickOnDish -> withContext(coroutineContext + parentJob) {
                database.getDishById(effect.dishId).collectLatest {
                    withContext(kotlin.coroutines.coroutineContext + Dispatchers.Main) {
                        navigateByCommand(
                            Nav.To(
                                R.id.action_global_nav_dish,
                                bundleOf(DISH_ID to it, DISH_AMOUNT to effect.amount), null
                            )
                        )
                    }
                }
            }
        }
    }
}
