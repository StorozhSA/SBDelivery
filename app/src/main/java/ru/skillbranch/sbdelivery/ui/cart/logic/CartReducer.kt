package ru.skillbranch.sbdelivery.ui.cart.logic

import kotlinx.coroutines.InternalCoroutinesApi
import ru.skillbranch.sbdelivery.ui.cart.logic.CartFeature.*
import ru.storozh.extensions.decZero
import ru.storozh.forcompose.Feature

@InternalCoroutinesApi
class CartReducer : Feature.Reducer<State, Msg, Eff> {
    override fun reduce(msg: Msg, state: State): Pair<State, Set<Eff>> = state.run {
        when (msg) {
            // Increment
            is Msg.IncrementCount -> {
                if (isIncDecInProcess) {
                    this to emptySet()
                } else {
                    copy(
                        isLoading = isLoading.inc(),
                        isIncDecInProcess = true
                    ) to setOf(Eff.IncrementItem(msg.dishId))
                }
            }
            is Msg.IncrementComplete -> copy(
                isLoading = isLoading.decZero(),
                isIncDecInProcess = false
            ) to emptySet()

            // Decrement
            is Msg.DecrementCount -> {
                if (isIncDecInProcess) {
                    this to emptySet()
                } else {
                    copy(
                        isLoading = isLoading.inc(),
                        isIncDecInProcess = true
                    ) to setOf(Eff.DecrementItem(msg.dishId))
                }
            }
            is Msg.DecrementComplete -> copy(
                isLoading = isLoading.decZero(),
                isIncDecInProcess = false
            ) to emptySet()

            // Remove dialog
            is Msg.ShowConfirm -> {
                if (isRemoveInProcess) {
                    this to emptySet()
                } else {
                    copy(
                        confirmDialog = StateConfirmDialog.Show(id = msg.id, title = msg.title)
                    ) to emptySet()
                }
            }
            is Msg.HideConfirm -> copy(confirmDialog = StateConfirmDialog.Hide) to emptySet()

            // Remove
            is Msg.RemoveFromCart -> {
                if (isRemoveInProcess) {
                    this to emptySet()
                } else {
                    copy(
                        isLoading = isLoading.inc(),
                        isRemoveInProcess = true,
                        // confirmDialog = StateConfirmDialog.Hide
                    ) to setOf(Eff.RemoveItem(msg.dishId))
                }
            }
            is Msg.RemoveFromCartComplete -> copy(
                isLoading = isLoading.decZero(),
                isRemoveInProcess = false,
                confirmDialog = StateConfirmDialog.Hide,

                ) to emptySet()

            // Send order
            is Msg.SendOrder -> {
                if (isSendOrderInProcess) {
                    this to emptySet()
                } else {
                    copy(
                        isLoading = isLoading.inc(),
                        isSendOrderInProcess = true
                    ) to setOf(Eff.SendOrder(msg.order))
                }
            }
            is Msg.SendOrderComplete -> {
                copy(
                    isLoading = isLoading.decZero(),
                    isSendOrderInProcess = false
                ) to emptySet()
            }
            is Msg.ClickOnDish -> {
                copy(
                    isLoading = isLoading.decZero()
                ) to setOf(Eff.ClickOnDish(msg.dishId, msg.title, msg.amount))
            }
        }
    }
}
