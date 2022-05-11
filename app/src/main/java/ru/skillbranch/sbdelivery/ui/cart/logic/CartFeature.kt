package ru.skillbranch.sbdelivery.ui.cart.logic

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import ru.skillbranch.sbdelivery.ui.cart.logic.CartFeature.*
import ru.storozh.forcompose.Feature
import ru.storozh.forcompose.ProtoFeature
import ru.storozh.models.delivery.database.domains.CartItemJoined
import java.io.Serializable

@InternalCoroutinesApi
class CartFeature(
    initialState: State = State(),
    effectHandler: CartHandler,
    reducer: CartReducer,
    scope: CoroutineScope
) : ProtoFeature<State, Msg, Eff, CartHandler>(initialState, effectHandler, reducer, scope) {

    data class State(
        val isLoading: Int = 0,
        val isUserAuth: Boolean = false,
        val promo: String = "",
        val confirmDialog: StateConfirmDialog = StateConfirmDialog.Hide,
        val list: StateUi = StateUi.Empty,
        val isIncDecInProcess: Boolean = false,
        val isRemoveInProcess: Boolean = false,
        val isSendOrderInProcess: Boolean = false
    ) : Feature.State {
        fun isLoading(): Boolean = isLoading > 0
    }

    sealed class StateUi : Serializable {
        data class Value(val dishes: List<CartItemJoined>) : StateUi()
        object Empty : StateUi()
        object Loading : StateUi()
    }

    sealed class StateConfirmDialog : Serializable {
        data class Show(val id: String, val title: String) : StateConfirmDialog()
        object Hide : StateConfirmDialog()
    }

    sealed class Eff : Feature.Effect {
        data class IncrementItem(val dishId: String) : Eff()
        data class DecrementItem(val dishId: String) : Eff()
        data class RemoveItem(val dishId: String) : Eff()
        data class SendOrder(val order: Map<String, Int>) : Eff()
        data class ClickOnDish(val dishId: String, val title: String, val amount: Int) : Eff()
    }

    sealed class Msg : Feature.Message {
        object HideConfirm : Msg()
        data class SendOrder(val order: Map<String, Int>) : Msg()
        object SendOrderComplete : Msg()
        data class ShowConfirm(val id: String, val title: String) : Msg()
        data class IncrementCount(val dishId: String) : Msg()
        data class IncrementComplete(val dishId: String) : Msg()
        data class DecrementCount(val dishId: String) : Msg()
        data class DecrementComplete(val dishId: String) : Msg()
        data class RemoveFromCart(val dishId: String) : Msg()
        data class RemoveFromCartComplete(val dishId: String) : Msg()
        data class ClickOnDish(val dishId: String, val title: String, val amount: Int) : Msg()
    }
}
