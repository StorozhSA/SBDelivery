package ru.skillbranch.sbdelivery.ui.dish

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.skillbranch.sbdelivery.AppSharedPreferencesHelper
import ru.skillbranch.sbdelivery.data.EndPointsImpl
import ru.skillbranch.sbdelivery.data.RepoDataBase
import ru.skillbranch.sbdelivery.data.RepoNetwork
import ru.skillbranch.sbdelivery.ui.dish.DishFeature.Eff
import ru.skillbranch.sbdelivery.ui.dish.DishFeature.Msg
import ru.storozh.common.AppSharedPreferences
import ru.storozh.common.EndPoints
import ru.storozh.common.Notify
import ru.storozh.forcompose.ProtoHandler
import ru.storozh.models.delivery.network.domains.ReqReview
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@InternalCoroutinesApi
class DishHandler @Inject constructor(
    private val database: RepoDataBase,
    private val network: RepoNetwork,
    private val point: EndPoints,
    private val pref: AppSharedPreferences
) : ProtoHandler<Eff, Msg>() {
    val points by lazy { point as EndPointsImpl }
    val prefs by lazy { pref as AppSharedPreferencesHelper }

    override suspend fun handle(effect: Eff) {
        when (effect) {
            is Eff.Increment -> withContext(coroutineContext + parentJob) {
                database.incrementCartItem(effect.dishId)
                delay(700)
                // commit(CartFeature.Msg.IncrementComplete(effect.dishId))
                commit(Msg.HideProgress)
            }

            is Eff.Decrement -> withContext(coroutineContext + parentJob) {
                database.decrementCartItem(effect.dishId)
                delay(700)
                // commit(CartFeature.Msg.DecrementComplete(effect.dishId))
                commit(Msg.HideProgress)
            }

            is Eff.AddToBasket -> commit(Msg.HideProgress)
            is Eff.ToggleLike -> withContext(coroutineContext + parentJob) {
                commit(Msg.ToggleLikeComplete(database.toggleFavoriteDishSuspend(effect.dishId)))
            }
            is Eff.UpdateReviews -> withContext(coroutineContext + parentJob) {
                launch {
                    points.immutableResReviewsFlow().collectLatest {
                        commit(Msg.UpdateReviewsComplete(effect.dishId))
                    }
                }
                launch {
                    network.getReviews(
                        dishId = effect.dishId,
                        offset = 0,
                        limit = 30,
                        oe = points.mutableResReviewsFlow(),
                        scope = this
                    )
                }
            }
            is Eff.UpdateReviewsComplete -> commit(Msg.LoadReviewsFromDb(effect.dishId))
            is Eff.LoadReviewsFromDb -> withContext(coroutineContext + parentJob) {
                database.getByDishId(effect.dishId).collectLatest {
                    commit(Msg.LoadReviewsFromDbComplete(it))
                }
            }
            is Eff.SendReview -> {
                // If user is login
                if (prefs.userIsAuth) {
                    withContext(coroutineContext + parentJob) {
                        launch {
                            points.immutableResAddReviewUnitFlow().collectLatest {
                                commit(Msg.SendReviewComplete(effect.dishId))
                            }
                        }
                        launch {
                            network.addReview(
                                ie = ReqReview(rating = effect.rating, text = effect.review),
                                oe = points.mutableResAddReviewUnitFlow(),
                                dishId = effect.dishId,
                                scope = this
                            )
                        }
                    }
                } else {
                    commit(Msg.SendReviewFailure(effect.dishId))
                    points.notify(Notify.TextMessage("User is not logged in"))
                }
            }
            is Eff.SendReviewComplete -> commit(Msg.UpdateReviews(effect.dishId))
            is Eff.PushTextMessage -> points.notify(Notify.TextMessage("User is not logged in"))
            is Eff.CancelCommonJob -> cancelChildrenJobs()
        }
    }
}
