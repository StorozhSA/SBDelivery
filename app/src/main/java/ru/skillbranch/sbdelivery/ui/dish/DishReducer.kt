package ru.skillbranch.sbdelivery.ui.dish

import kotlinx.coroutines.InternalCoroutinesApi
import ru.skillbranch.sbdelivery.ui.dish.DishFeature.*
import ru.storozh.forcompose.Feature

@InternalCoroutinesApi
class DishReducer : Feature.Reducer<State, Msg, Eff> {
    override fun reduce(msg: Msg, state: State): Pair<State, Set<Eff>> = state.run {
        when (msg) {
            is Msg.Increment -> {
                if (isIncrementInProcess) {
                    this to emptySet()
                } else {
                    copy(
                        isLoading = isLoading.inc(),
                        amount = amount.inc(),
                        isIncrementInProcess = true
                    ) to setOf(
                        Eff.Increment(msg.dishId)
                    )
                }
            }
            is Msg.Decrement -> {
                if (isDecrementInProcess) {
                    this to emptySet()
                } else {
                    copy(
                        isLoading = isLoading.inc(),
                        amount = amount.decZero(),
                        isDecrementInProcess = true
                    ) to setOf(
                        Eff.Decrement(msg.dishId)
                    )
                }
            }
            is Msg.HideProgress -> copy(
                isLoading = isLoading.decZero(),
                isBasketAddInProcess = false,
                isIncrementInProcess = false,
                isDecrementInProcess = false
            ) to emptySet()

            is Msg.AddToBasket -> {
                if (isBasketAddInProcess) {
                    this to emptySet()
                } else {
                    copy(
                        isLoading = isLoading.inc(),
                        isBasketAddInProcess = true
                    ) to setOf(
                        Eff.AddToBasket
                    )
                }
            }
            is Msg.ResultValue -> copy(amount = msg.value) to emptySet()

            //
            is Msg.ToggleLike -> {
                if (isToggleLikeInProcess) {
                    this to emptySet()
                } else {
                    copy(
                        isLoading = isLoading.inc(),
                        isToggleLikeInProcess = true
                    ) to setOf(
                        Eff.ToggleLike(msg.dishId)
                    )
                }
            }
            is Msg.ToggleLikeComplete -> copy(
                isFavorite = msg.isFavorite,
                isLoading = isLoading.decZero(),
                isToggleLikeInProcess = false
            ) to emptySet()

            //
            is Msg.UpdateReviews -> copy(
                isLoading = isLoading.inc(),
                isReviewLoadingInProcess = true
            ) to setOf(
                Eff.UpdateReviews(msg.dishId)
            )
            is Msg.UpdateReviewsComplete -> copy(
                isLoading = isLoading.decZero(),
                isReviewLoadingInProcess = true
            ) to setOf(
                Eff.UpdateReviewsComplete(msg.dishId)
            )

            //
            is Msg.LoadReviewsFromDb -> copy(
                isLoading = isLoading.inc(),
                isReviewLoadingInProcess = true
            ) to setOf(
                Eff.LoadReviewsFromDb(msg.dishId)
            )
            is Msg.LoadReviewsFromDbComplete -> copy(
                isLoading = isLoading.decZero(),
                isReviewLoadingInProcess = false,
                isReviewLoaded = true,
                reviews = msg.reviews,
                rating = if (msg.reviews.isNotEmpty()) msg.reviews.sumOf { it.rating }
                    .toFloat() / msg.reviews.size else 0f
            ) to setOf()

            //
            is Msg.ReviewDialogOpen -> {
                if (isUserAuth) {
                    copy(isReviewDialogShow = true) to setOf()
                } else {
                    this to setOf(
                        Eff.PushTextMessage("")
                    )
                }
            }
            is Msg.ReviewDialogClose -> copy(isReviewDialogShow = false) to setOf()

            //
            is Msg.SendReview -> copy(
                isLoading = isLoading.inc(),
                isReviewDialogShow = false
            ) to setOf(
                Eff.SendReview(msg.dishId, msg.rating, msg.review)
            )
            is Msg.SendReviewComplete -> copy(
                isLoading = isLoading.decZero(),
                isReviewDialogShow = false
            ) to setOf(
                Eff.SendReviewComplete(msg.dishId)
            )
            is Msg.SendReviewFailure -> copy(
                isLoading = isLoading.decZero(),
                isReviewDialogShow = false
            ) to setOf()
            is Msg.CancelCommonJob -> this to setOf(
                Eff.CancelCommonJob
            )
        }
    }

    private fun Int.decZero(): Int {
        return if (this.dec() > 0) this.dec() else 0
    }
}
