package ru.skillbranch.sbdelivery.ui.dish

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import ru.skillbranch.sbdelivery.ui.dish.DishFeature.*
import ru.storozh.forcompose.Feature
import ru.storozh.forcompose.ProtoFeature
import ru.storozh.models.delivery.database.domains.Reviews

@InternalCoroutinesApi
class DishFeature(
    initialState: State = State(),
    effectHandler: DishHandler,
    reducer: DishReducer,
    scope: CoroutineScope
) : ProtoFeature<State, Msg, Eff, DishHandler>(initialState, effectHandler, reducer, scope) {

    data class State(
        val amount: Int = 1,
        val isLoading: Int = 0,
        val isFavorite: Boolean = false,
        val reviews: List<Reviews> = emptyList(),
        val rating: Float = 0f,
        val isReviewLoaded: Boolean = false,
        val isReviewLoadingInProcess: Boolean = false,
        val isReviewDialogShow: Boolean = false,
        val isUserAuth: Boolean = false,
        val isBasketAddInProcess: Boolean = false,
        val isToggleLikeInProcess: Boolean = false,
        val isIncrementInProcess: Boolean = false,
        val isDecrementInProcess: Boolean = false

    ) : Feature.State {
        fun isLoading(): Boolean = isLoading > 0
    }

    sealed class Msg : Feature.Message {
        data class Increment(val dishId: String) : Msg()
        data class Decrement(val dishId: String) : Msg()
        object AddToBasket : Msg()
        object HideProgress : Msg()
        data class ToggleLike(val dishId: String) : Msg()
        data class ToggleLikeComplete(val isFavorite: Boolean) : Msg()
        data class ResultValue(val value: Int) : Msg()
        data class UpdateReviews(val dishId: String) : Msg()
        data class UpdateReviewsComplete(val dishId: String) : Msg()
        data class LoadReviewsFromDb(val dishId: String) : Msg()
        data class LoadReviewsFromDbComplete(val reviews: List<Reviews>) : Msg()
        data class ReviewDialogOpen(val dishId: String) : Msg()
        object ReviewDialogClose : Msg()
        data class SendReview(val dishId: String, val rating: Int, val review: String) : Msg()
        data class SendReviewComplete(val dishId: String) : Msg()
        data class SendReviewFailure(val dishId: String) : Msg()
        object CancelCommonJob : Msg()
    }

    sealed class Eff : Feature.Effect {
        data class Increment(val dishId: String) : Eff()
        data class Decrement(val dishId: String) : Eff()
        object AddToBasket : Eff()
        data class ToggleLike(val dishId: String) : Eff()
        data class UpdateReviews(val dishId: String) : Eff()
        data class UpdateReviewsComplete(val dishId: String) : Eff()
        data class LoadReviewsFromDb(val dishId: String) : Eff()
        data class SendReview(val dishId: String, val rating: Int, val review: String) : Eff()
        data class SendReviewComplete(val dishId: String) : Eff()
        data class PushTextMessage(val text: String) : Eff()
        object CancelCommonJob : Eff()
    }
}
