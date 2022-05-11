@file:Suppress("unused", "unused", "unused")

package ru.skillbranch.sbdelivery.data

import androidx.paging.PagingSource
import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import okhttp3.internal.toImmutableList
import ru.skillbranch.sbdelivery.AppSharedPreferencesHelper
import ru.skillbranch.sbdelivery.SORT_BY_ALPHABET
import ru.skillbranch.sbdelivery.SORT_BY_POPULAR
import ru.skillbranch.sbdelivery.SORT_BY_RATING
import ru.skillbranch.sbdelivery.hilt.DeliveryServiceDataBase
import ru.storozh.common.AppException
import ru.storozh.common.AppSharedPreferences
import ru.storozh.common.EndPoints
import ru.storozh.common.Notify
import ru.storozh.common.network.FOR_DATA
import ru.storozh.common.network.Result
import ru.storozh.extensions.decZero
import ru.storozh.extensions.logd
import ru.storozh.extensions.loge
import ru.storozh.models.delivery.database.DeliveryDatabaseService
import ru.storozh.models.delivery.database.domains.*
import ru.storozh.models.delivery.database.domains.CartItem
import ru.storozh.models.delivery.network.domains.*
import javax.annotation.concurrent.ThreadSafe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ThreadSafe
class RepoDataBase @Inject constructor(
    @DeliveryServiceDataBase private val service: DeliveryDatabaseService,
    point: EndPoints,
    pref: AppSharedPreferences
) {
    private val points by lazy { point as EndPointsImpl }
    private val prefs by lazy { pref as AppSharedPreferencesHelper }

    // Default error handler for coroutines
    private val handler = CoroutineExceptionHandler { _, ex ->
        loge(ex.localizedMessage ?: "Not found error message text")
        when (ex) {
            is AppException -> {
                CoroutineScope(Dispatchers.Main).launch {
                    points.mNotifyFlow().emit(Notify.ErrorMessage(ex.error, "", null))
                }
            }
            else -> {
                CoroutineScope(Dispatchers.Main).launch {
                    points.mNotifyFlow().emit(
                        Notify.ErrorMessage(
                            AppError.E_UNKNOWN.name,
                            ex.localizedMessage ?: "",
                            null
                        )
                    )
                }
            }
        }
    }

    fun saveProfileIsRegister(value: Result.Success.Value<ResRegister>): Job {
        return CoroutineScope(Dispatchers.IO).launch(handler) {
            service.runInTransaction {
                service.userDao().delete()
                service.userDao().upsert(
                    listOf(
                        User(
                            id = value.payload.id,
                            accessToken = value.payload.accessToken,
                            refreshToken = value.payload.refreshToken,
                            email = value.payload.email,
                            firstName = value.payload.firstName,
                            lastName = value.payload.lastName
                        )
                    )
                )
            }
        }
    }

    fun saveProfileIsLogin(value: Result.Success.Value<ResLogin>): Job {
        return CoroutineScope(Dispatchers.IO).launch(handler) {
            service.runInTransaction {
                // service.userProfileDao().delete()
                service.userDao().upsert(
                    listOf(
                        User(
                            id = value.payload.id,
                            accessToken = value.payload.accessToken,
                            refreshToken = value.payload.refreshToken,
                            email = value.payload.email,
                            firstName = value.payload.firstName,
                            lastName = value.payload.lastName
                        )
                    )
                )
            }
        }
    }

    fun saveProfileIsEdit(value: Result.Success.Value<ResUserProfile>): Job {
        return CoroutineScope(Dispatchers.IO).launch(handler) {
            service.runInTransaction {
                service.userDao().upsert(
                    listOf(
                        User(
                            id = value.payload.id,
                            email = value.payload.email,
                            firstName = value.payload.firstName,
                            lastName = value.payload.lastName
                        )
                    )
                )
            }
        }
    }

    fun saveAccessToken(value: Result.Success.Value<ResRecoveryToken>): Job {
        return CoroutineScope(Dispatchers.IO).launch(handler) {
            service.runInTransaction {
                service.userDao().upsert(
                    listOf(
                        User(
                            accessToken = value.payload.accessToken
                        )
                    )
                )
            }
        }
    }

    fun saveCategories(value: ResDishCategories): Job {
        return CoroutineScope(Dispatchers.IO).launch(handler) {
            service.runInTransaction {
                service.categoryDao().upsert(
                    value.map {
                        Category(
                            id = it.id ?: "0",
                            name = it.name ?: "",
                            order = it.order ?: 0,
                            icon = it.icon ?: "",
                            parent = it.parent ?: "root",
                            active = it.active ?: false,
                            createdAt = it.createdAt ?: 0,
                            updatedAt = it.updatedAt ?: 0
                        )
                    }
                )
            }
        }
    }

    fun getFlowCategories(catId: String = "root"): Flow<List<Category>> =
        service.categoryDao().getByParentIdFlow(catId)

    fun getFlowCategoriesView(catId: String = "root"): Flow<List<CategoryV>> =
        service.categoryVDao().getByParentIdFlow(catId)

    fun getDishById(dishId: String): Flow<DishV> = service.dishVDao().getById(dishId = dishId)

    fun saveDishes(value: ResDishes): Job {
        return CoroutineScope(Dispatchers.IO).launch(handler) {
            service.runInTransaction {
                service.dishDao().upsert(
                    value.map {
                        Dish(
                            id = it.id ?: "0",
                            name = it.name ?: "",
                            description = it.description ?: "",
                            image = it.image ?: "",
                            oldPrice = it.oldPrice ?: 0,
                            price = it.price ?: 0,
                            rating = it.rating ?: 0.0,
                            commentsCount = it.commentsCount ?: 0,
                            likes = it.likes ?: 0,
                            category = it.category ?: "",
                            active = it.active ?: false,
                            createdAt = it.createdAt ?: 0,
                            updatedAt = it.updatedAt ?: 0,
                            favorite = false
                        )
                    }
                )
            }
        }
    }

    fun getDishesPagingSource(
        catId: String,
        sortMode: String = SORT_BY_ALPHABET,
        sortDirect: Boolean = false
    ): PagingSource<Int, DishV> {
        val d = when (sortDirect) {
            false -> "ASC"
            true -> "DESC"
        }
        val m = when (sortMode) {
            SORT_BY_ALPHABET -> "name"
            SORT_BY_POPULAR -> "likes"
            SORT_BY_RATING -> "rating"
            else -> " name "
        }

        val q = "SELECT * FROM DishV WHERE active=1 AND category='$catId' ORDER BY $m $d"
        logd("invoke getDishesPagingSource query = $q")
        return service.dishVDao().getByParentIdViewPagingSource(SimpleSQLiteQuery(q))
    }

    fun getLastModDateDishes(): Long = service.dishDao().getLastModDate()
    fun getLastModDateCategory(): Long = service.categoryDao().getLastModDate()

    fun searchCategories(text: String) = service.categoryVDao().find("%$text%")
    fun searchDishes(text: String) = service.dishVDao().find("%$text%")

    //region Search history
    fun getSearchHistory(): Flow<List<String>> = service.searchHistoryDao().getHistory()

    fun addSearchHistoryItem(value: String): Job {
        return CoroutineScope(Dispatchers.IO).launch(handler) {
            service.runInTransaction {
                service.searchHistoryDao().cleanOldItems()
                service.searchHistoryDao().upsert(
                    listOf(
                        SearchHistory(id = value)
                    )
                )
            }
        }
    }

    fun delete(id: String): Job {
        return CoroutineScope(Dispatchers.IO).launch(handler) {
            service.runInTransaction {
                service.searchHistoryDao().delete(id)
            }
        }
    }
    //endregion

    //region Recommendation dishes
    fun saveRecommendationDishes(value: Result.Success.Value<Set<String>>): Job {
        return CoroutineScope(Dispatchers.IO).launch(handler) {
            service.runInTransaction {
                service.dishesRecommendationDao().delete()
                service.dishesRecommendationDao().upsert(
                    value.payload.map { item -> DishRecommendation(id = item) }.toImmutableList()
                )
            }
        }
    }

    fun cleanRecommendationDishes(): Job {
        logd("Invoke cleanRecommendationDishes()")
        return CoroutineScope(Dispatchers.IO).launch(handler) {
            service.runInTransaction {
                service.dishesRecommendationDao().delete()
            }
        }
    }

    fun getAllRecommendationDishes() = service.dishVDao().getAllRecommendation()
    fun getAllBestDishes(minRating: Double) = service.dishVDao().getAllBest(minRating)
    fun getAllPopularDishes() = service.dishVDao().getAllPopular()

    //endregion

    //region Favorite dishes
    fun saveFavoriteDishes(value: Set<ResFavoriteItem>): Job {
        return CoroutineScope(Dispatchers.IO).launch(handler) {
            service.runInTransaction {
                // todo
                //  service.dishDao().upsert(
                // value.payload.map { item -> DishFavorite(id = item) }.toImmutableList()
                //  )
            }
        }
    }

    fun toggleFavoriteDish(id: String): Job {
        return CoroutineScope(Dispatchers.IO).launch(handler) {
            service.runInTransaction {
                service.dishDao().getById(id).let {
                    service.dishDao().upsert(listOf(it.copy(favorite = !it.favorite)))
                }
            }
        }
    }

    fun toggleFavoriteDishSuspend(id: String): Boolean {
        var res = false
        service.runInTransaction {
            service.dishDao().getById(id).let {
                res = !it.favorite
                service.dishDao().upsert(listOf(it.copy(favorite = res)))
            }
        }
        return res
    }

    // fun getAllFavoriteDishes() = service.dishVDao().getAllFavorites()
    fun getAllFavoriteDishes(
        oe: MutableSharedFlow<List<DishV>> = points.mutableFavoriteDbFlow(),
        scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    ): Job {
        return scope.launch(Dispatchers.IO + handler) {
            oe.emitAll(service.dishVDao().getAllFavorites())
        }
    }

    //endregion

    //region Reviews dishes
    fun saveReviews(value: Result.Success.Value<ResReviews>): Job {
        return CoroutineScope(Dispatchers.IO).launch(handler) {
            service.runInTransaction {
                service.reviewsDao().upsert(
                    value.payload.map {
                        Reviews(
                            id = it.id,
                            dishId = value.techHeaders[FOR_DATA] ?: "",
                            author = it.author,
                            text = it.text,
                            date = it.date,
                            rating = it.rating,
                            active = it.active,
                            createdAt = it.createdAt,
                            updatedAt = it.updatedAt
                        )
                    }
                )
            }
        }
    }

    fun getByDishId(dishId: String): Flow<List<Reviews>> = service.reviewsDao().getByDishId(dishId)
    //endregion

    //region CartItem
    fun getCart(
        oe: MutableSharedFlow<List<CartItemJoined>> = points.mutableCartItemJoinedFlow(),
        scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    ): Job {
        return scope.launch(Dispatchers.IO + handler) {
            oe.emitAll(service.cartDao().getCart())
        }
    }

    fun saveCart(value: List<CartItem>): Job {
        return CoroutineScope(Dispatchers.IO).launch(handler) {
            service.runInTransaction {
                service.cartDao().upsert(value)
            }
        }
    }

    fun incrementCartItem(dishId: String): Job {
        return CoroutineScope(Dispatchers.IO).launch(handler) {
            service.runInTransaction {
                service.cartDao().getCart(dishId = dishId)?.let {
                    service.cartDao().upsert(listOf(it.asCartItem().copy(amount = it.amount.inc())))
                } ?: run {
                    throw AppException(AppError.E_NOT_FOUND.name)
                }
            }
        }
    }

    fun decrementCartItem(dishId: String): Job {
        return CoroutineScope(Dispatchers.IO).launch(handler) {
            service.runInTransaction {
                service.cartDao().getCart(dishId = dishId)?.let {
                    service.cartDao()
                        .upsert(listOf(it.asCartItem().copy(amount = it.amount.decZero())))
                } ?: run {
                    throw AppException(AppError.E_NOT_FOUND.name)
                }
            }
        }
    }

    fun removeCartItem(dishId: String): Job {
        return CoroutineScope(Dispatchers.IO).launch(handler) {
            service.runInTransaction {
                service.cartDao().delete(id = dishId).let {
                    if (it == 0) {
                        throw AppException(AppError.E_NOT_FOUND.name)
                    }
                }
            }
        }
    }

    //endregion
}
