@file:Suppress("unused", "unused", "unused", "MemberVisibilityCanBePrivate")

package ru.skillbranch.sbdelivery.data

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import ru.skillbranch.sbdelivery.AppSharedPreferencesHelper
import ru.skillbranch.sbdelivery.data.AppError.*
import ru.skillbranch.sbdelivery.hilt.DeliveryServiceNetwork
import ru.storozh.common.AppException
import ru.storozh.common.AppSharedPreferences
import ru.storozh.common.EndPoints
import ru.storozh.common.Notify
import ru.storozh.common.network.Consider
import ru.storozh.common.network.ConsiderVariants.*
import ru.storozh.common.network.Result
import ru.storozh.common.network.RetrofitService
import ru.storozh.extensions.logd
import ru.storozh.extensions.loge
import ru.storozh.models.delivery.network.DeliveryAPI
import ru.storozh.models.delivery.network.DeliveryConnect
import ru.storozh.models.delivery.network.domains.*
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import javax.annotation.concurrent.ThreadSafe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ThreadSafe
class RepoNetwork @Inject constructor(
    @DeliveryServiceNetwork
    private val service: RetrofitService<DeliveryConnect, DeliveryAPI>,
    point: EndPoints,
    pref: AppSharedPreferences
) {
    private val points by lazy { point as EndPointsImpl }
    private val prefs by lazy { pref as AppSharedPreferencesHelper }
    private val stub = MutableSharedFlow<Boolean>(
        replay = 0,
        extraBufferCapacity = 3,
        BufferOverflow.DROP_OLDEST
    )

    // Default error handler for corutines
    private val handler = CoroutineExceptionHandler { _, ex ->
        loge(ex.localizedMessage ?: "Not found error message text")

        when (ex) {
            is AppException -> {
                CoroutineScope(Dispatchers.Main).launch {
                    points.mNotifyFlow().emit(Notify.ErrorMessage(ex.error, "", null))
                }

                // Refresh token manually if the authenticator fails
                if (ex.errorHttp == 401) {
                    refresh(
                        ReqRecoveryToken(refreshToken = prefs.refreshToken),
                        points.mutableResRecoveryTokenFlow(),
                        CoroutineScope(Dispatchers.IO)
                    )
                }
            }
            else -> {
                CoroutineScope(Dispatchers.Main).launch {
                    points.mNotifyFlow()
                        .emit(Notify.ErrorMessage(E_UNKNOWN.name, ex.localizedMessage ?: "", null))
                }
            }
        }
    }

    // Auth token
    private fun getTokenAsBearer(): String = "bearer ${prefs.accessToken}"

    /**
     * Login - https://sbdelivery.docs.apiary.io/reference/0/0/login POST
     * @param ie - Login data
     * @param oe - The response data is written to the provided LiveData
     */
    fun login(
        ie: ReqLogin,
        oe: MutableSharedFlow<Result<ResLogin>>,
        scope: CoroutineScope
    ): Job {
        logd("fun login() invoke")

        return service.send(
            oe, handler,
            setOf(
                Consider(Y, 201, E_SUCCESS.name),
                Consider(E, 0, E_LOGIN_FAILED.name),
                Consider(E, 402, E_LOGIN_ERROR.name)
            ),
            points.mLoad(),
            scope
        ) {
            service.api.login(ie)
        }
    }

    /**
     * Register - https://sbdelivery.docs.apiary.io/reference/0/1/register POST
     * @param ie - Register data
     * @param oe - The response data is written to the provided LiveData
     */
    fun register(
        ie: ReqRegister,
        oe: MutableSharedFlow<Result<ResRegister>>,
        scope: CoroutineScope
    ): Job {
        logd("fun register() invoke")

        return service.send(
            oe, handler,
            setOf(
                Consider(Y, 201, E_SUCCESS.name),
                Consider(E, 0, E_REG_FAILED.name),
                Consider(E, 400, E_REG_EXISTS.name)
            ),
            points.mLoad(), scope
        ) {
            service.api.register(ie)
        }
    }

    /**
     * Recovery email - https://polls.apiblueprint.org/auth/recovery/email POST
     * @param ie - Recovery data
     * @param oe - The response data is written to the provided LiveData
     */
    fun recovery(
        ie: ReqRecoveryEmail,
        oe: MutableSharedFlow<Result<Unit>>,
        scope: CoroutineScope
    ): Job {
        logd("fun recovery() Email invoke")

        return service.send(
            oe, handler,
            setOf(
                Consider(Y, 200, E_SUCCESS.name),
                // TODO Уточнить в разных ЛК разное значение
                Consider(E, 201, E_RECOVERY_EMAIL_FAILED.name),
                Consider(E, 0, E_RECOVERY_EMAIL_FAILED.name),
                Consider(E, 400, E_RECOVERY_EMAIL_LESS_TIME.name)
            ),
            points.mLoad(), scope
        ) {
            service.api.recovery(ie)
        }
    }

    /**
     * Recovery code - https://polls.apiblueprint.org/auth/recovery/code POST
     * @param ie - Recovery data
     * @param oe - The response data is written to the provided LiveData
     */
    fun recovery(
        ie: ReqRecoveryCode,
        oe: MutableSharedFlow<Result<Unit>>,
        scope: CoroutineScope
    ): Job {
        logd("fun recovery() Code invoke")

        return service.send(
            oe, handler,
            setOf(
                Consider(Y, 200, E_SUCCESS.name),
                // TODO Уточнить в разных ЛК разное значение
                Consider(E, 201, E_RECOVERY_CODE_FAILED.name),
                Consider(E, 0, E_RECOVERY_CODE_FAILED.name),
                Consider(E, 400, E_RECOVERY_CODE_WRONG.name)
            ),
            points.mLoad(), scope
        ) {
            service.api.recovery(ie)
        }
    }

    /**
     * Recovery password - https://polls.apiblueprint.org/auth/recovery/password POST
     * @param ie - Recovery data
     * @param oe - The response data is written to the provided LiveData
     */
    fun recovery(
        ie: ReqRecoveryPassword,
        oe: MutableSharedFlow<Result<Unit>>,
        scope: CoroutineScope
    ): Job {
        logd("fun recovery() Password invoke")

        return service.send(
            oe, handler,
            setOf(
                Consider(Y, 200, E_SUCCESS.name),
                // TODO Уточнить в разных ЛК разное значение
                Consider(E, 201, E_RECOVERY_PASSWORD_FAILED.name),
                Consider(E, 0, E_RECOVERY_PASSWORD_FAILED.name),
                Consider(E, 402, E_RECOVERY_PASSWORD_EXPIRED.name)
            ),
            points.mLoad(), scope
        ) {
            service.api.recovery(ie)
        }
    }

    /**
     * Refresh - https://polls.apiblueprint.org/auth/refresh POST
     * @param ie - Refresh data
     * @param oe - The response data is written to the provided LiveData
     */
    fun refresh(
        ie: ReqRecoveryToken,
        oe: MutableSharedFlow<Result<ResRecoveryToken>>,
        scope: CoroutineScope
    ): Job {
        logd("fun refresh() token invoke")

        return service.send(
            oe, handler,
            setOf(
                Consider(Y, 201, E_SUCCESS.name),
                Consider(E, 0, E_REFRESH_TOKEN_FAILED.name),
                Consider(E, 402, E_REFRESH_TOKEN_EXPIRED.name)
            ),
            points.mLoad(), scope
        ) {
            service.api.refresh(ie)
        }
    }

    /**
     * Profile - https://polls.apiblueprint.org/profile GET
     * @param oe - The response data is written to the provided LiveData
     */
    fun profile(oe: MutableSharedFlow<Result<ResUserProfile>>, scope: CoroutineScope): Job {
        logd("fun profile() get invoke")

        return service.send(
            oe, handler,
            setOf(
                Consider(Y, 200, E_SUCCESS.name),
                Consider(E, 0, E_PROFILE_GET_FAILED.name),
                Consider(E, 401, E_PROFILE_GET_FAILED.name), // TODO Узнать что за ошибка
                Consider(E, 402, E_PROFILE_GET_FAILED.name) // TODO
            ),
            points.mLoad(), scope
        ) {
            service.api.profile(getTokenAsBearer())
            // service.api.profile()
        }
    }

    /**
     * Profile - https://polls.apiblueprint.org/profile PUT
     * @param ie - Profile data
     * @param oe - The response data is written to the provided LiveData
     */
    fun profile(
        ie: ReqUserProfile,
        oe: MutableSharedFlow<Result<ResUserProfile>>,
        scope: CoroutineScope
    ): Job {
        logd("fun profile() edit invoke")

        return service.send(
            oe, handler,
            setOf(
                Consider(Y, 202, E_SUCCESS.name),
                Consider(E, 0, E_PROFILE_GET_FAILED.name),
                Consider(E, 401, E_PROFILE_GET_FAILED.name), // TODO Узнать что за ошибка
                Consider(E, 402, E_PROFILE_GET_FAILED.name) // TODO
            ),
            points.mLoad(), scope
        ) {
            service.api.profile(ie, getTokenAsBearer())
        }
    }

    /**
     * Change password - https://polls.apiblueprint.org/profile/password PUT
     * @param ie - Password data
     * @param oe - The response data is written to the provided LiveData
     */
    fun changePassword(
        ie: ReqNewPassword,
        oe: MutableSharedFlow<Result<Unit>>,
        scope: CoroutineScope
    ): Job {
        logd("fun changePassword() invoke")

        return service.send(
            oe, handler,
            setOf(
                Consider(Y, 202, E_SUCCESS.name),
                Consider(E, 0, E_PASSWORD_CHANGE_FAILED.name),
                Consider(E, 400, E_PASSWORD_CHANGE_FAILED.name), // TODO Узнать что за ошибка
                Consider(E, 401, E_PASSWORD_CHANGE_FAILED.name), // TODO
                Consider(E, 402, E_PASSWORD_CHANGE_FAILED.name) // TODO
            ),
            points.mLoad(), scope
        ) {
            service.api.changePassword(ie, getTokenAsBearer())
        }
    }

    /**
     * Favorite - https://polls.apiblueprint.org/favorite GET
     * @param oe - The response data is written to the provided LiveData
     * @param date - Date for header If-Modified-Since
     */
    fun favorite(
        oe: MutableSharedFlow<Result<ResFavorite>>,
        offset: Int = 0,
        limit: Int = 10,
        date: Date,
        scope: CoroutineScope
    ): Job {
        logd("fun favorite() get invoke")

        return service.send(
            oe, handler,
            setOf(
                Consider(Y, 200, E_SUCCESS.name),
                Consider(E, 0, E_FAVORITE_FAILED.name),
                Consider(N, 304, E_NOT_MODIFIED.name),
                Consider(E, 401, E_FAVORITE_FAILED.name), // TODO
                Consider(E, 402, E_FAVORITE_FAILED.name) // TODO
            ),
            points.mLoad(), scope
        ) {
            service.api.favorite(offset, limit, getTokenAsBearer(), date)
        }
    }

    /**
     * Favorite - https://polls.apiblueprint.org/favorite PUT
     * @param ie - Favorite data
     * @param oe - The response data is written to the provided LiveData
     */
    fun favorite(
        ie: ReqFavorite,
        oe: MutableSharedFlow<Result<Unit>>,
        scope: CoroutineScope
    ): Job {
        logd("fun favorite() put invoke")

        return service.send(
            oe, handler,
            setOf(
                Consider(Y, 202, E_SUCCESS.name),
                Consider(E, 0, E_FAVORITE_FAILED.name),
                Consider(E, 401, E_FAVORITE_FAILED.name), // TODO Узнать что за ошибка
                Consider(E, 402, E_FAVORITE_FAILED.name) // TODO
            ),
            points.mLoad(), scope
        ) {
            service.api.favorite(ie, getTokenAsBearer())
        }
    }

    /**
     * getRecommended - https://polls.apiblueprint.org/main/recommend GET
     * @param oe - The response data is written to the provided LiveData
     */
    fun getRecommended(oe: MutableSharedFlow<Result<Set<String>>>, scope: CoroutineScope): Job {
        logd("fun getRecommended() invoke")

        return service.send(
            oe, handler,
            setOf(
                Consider(Y, 200, E_SUCCESS.name),
                Consider(E, 0, E_GET_RECOMMENDED_FAILED.name)
            ),
            points.mLoad(), scope
        ) {
            service.api.getRecommended()
        }
    }

    /**
     * Categories - https://polls.apiblueprint.org/categories GET
     * @param oe - The response data is written to the provided LiveData
     * @param date - Date for header If-Modified-Since
     */
    fun getCategories(
        offset: Int = 0,
        limit: Int = 10,
        date: Date,
        oe: MutableSharedFlow<Result<ResDishCategories>>,
        scope: CoroutineScope
    ): Job {
        logd("fun getCategories() invoke")

        return service.send(
            oe, handler,
            setOf(
                Consider(Y, 200, E_SUCCESS.name),
                Consider(E, 0, E_GET_CATEGORIES_FAILED.name),
                Consider(N, 304, E_NOT_MODIFIED.name)
            ),
            points.mLoad(), scope
        ) {
            service.api.getCategories(offset, limit, date)
        }
    }

    suspend fun getCategoriesAll(
        lastModified: Long = 0,
        scope: CoroutineScope
    ): Flow<ResDishCategories> =
        flow {
            println("Flow getCategoriesAll started")
            val limit = 1
            val offset = AtomicInteger(0)
            val tmp = MutableSharedFlow<Result<ResDishCategories>>(replay = 0)
            getCategories(
                offset = offset.getAndIncrement(),
                limit = limit,
                date = Date(lastModified),
                tmp,
                scope
            )
            tmp.collect {
                if (it is Result.Success.Value) {
                    emit(it.payload)
                    if (it.payload.size == limit) {
                        getCategories(
                            offset = offset.getAndIncrement(),
                            limit = limit,
                            date = Date(lastModified),
                            tmp,
                            scope
                        )
                    }
                } else {
                    emit(ResDishCategories())
                }
            }
        }

    /**
     * Dishes - https://polls.apiblueprint.org/dishes?offset=0&limit=10 GET
     * @param oe - The response data is written to the provided LiveData
     * @param date - Date for header If-Modified-Since
     */
    fun getDishes(
        offset: Int = 0,
        limit: Int = 10,
        date: Date,
        oe: MutableSharedFlow<Result<ResDishes>>,
        scope: CoroutineScope
    ): Job {
        logd("fun getDishes() invoke. Last modified date = $date")

        return service.send(
            oe, handler,
            setOf(
                Consider(Y, 200, E_SUCCESS.name),
                Consider(E, 0, E_GET_DISHES_FAILED.name),
                Consider(N, 304, E_NOT_MODIFIED.name)
            ),
            points.mLoad(), scope
        ) {
            service.api.getDishes(offset, limit, date)
        }
    }

    suspend fun getDishesAll(lastModified: Long = 0, scope: CoroutineScope): Flow<ResDishes> =
        flow {
            logd("Flow getDishesAll started")
            val limit = 1
            val offset = AtomicInteger(0)
            val tmp = MutableSharedFlow<Result<ResDishes>>(replay = 0)
            getDishes(
                offset = offset.getAndIncrement(),
                limit = limit,
                date = Date(lastModified),
                tmp,
                scope
            )
            tmp.collect {
                if (it is Result.Success.Value) {
                    emit(it.payload)
                    if (it.payload.size == limit) {
                        getDishes(
                            offset = offset.getAndIncrement(),
                            limit = limit,
                            date = Date(lastModified),
                            tmp,
                            scope
                        )
                    }
                }
            }
        }

    /**
     * Reviews - https://polls.apiblueprint.org/reviews/dishId?offset=0&limit=10 GET
     * @param oe - The response data is written to the provided LiveData
     * @param dishId - ID dish
     */
    fun getReviews(
        dishId: String,
        offset: Int = 0,
        limit: Int = 10,
        oe: MutableSharedFlow<Result<ResReviews>>,
        scope: CoroutineScope
    ): Job {
        logd("fun getReviews() invoke")

        return service.send(
            oe, handler,
            setOf(
                Consider(Y, 200, E_SUCCESS.name),
                Consider(E, 0, E_GET_REVIEWS_FAILED.name),
                Consider(N, 304, E_NOT_MODIFIED.name)
            ),
            stub, scope
        ) {
            service.api.getReviews(dishId, offset, limit)
        }
    }

    /**
     * Reviews - https://polls.apiblueprint.org/reviews/dishId POST
     * @param ie - Review data
     * @param oe - The response data is written to the provided LiveData
     * @param dishId - ID dish
     */
    fun addReview(
        ie: ReqReview,
        oe: MutableSharedFlow<Result<Unit>>,
        dishId: String,
        scope: CoroutineScope
    ): Job {
        logd("fun addReview() invoke")

        return service.send(
            oe, handler,
            setOf(
                Consider(Y, 201, E_SUCCESS.name),
                Consider(E, 0, E_ADD_REVIEWS_FAILED.name),
                Consider(E, 400, E_ADD_REVIEWS_FAILED.name),
                Consider(E, 401, E_ADD_REVIEWS_FAILED.name) // TODO
            ),
            stub, scope
        ) {
            service.api.addReview(dishId, ie, getTokenAsBearer())
        }
    }

    /**
     * getUpdateCart - https://polls.apiblueprint.org/cart GET or PUT
     * @param ie - CartItem data for update
     * @param oe - The response data is written to the provided LiveData
     */
    fun getUpdateCart(
        ie: ReqCart? = null,
        oe: MutableSharedFlow<Result<ResCart>>,
        scope: CoroutineScope
    ): Job {
        logd("fun getUpdateCart() invoke")

        return service.send(
            oe, handler,
            setOf(
                Consider(Y, 200, E_SUCCESS.name),
                Consider(Y, 202, E_SUCCESS.name),
                Consider(E, 0, E_GET_OR_UPDATE_CART_FAILED.name),
                Consider(E, 400, E_GET_OR_UPDATE_CART_FAILED.name),
                Consider(E, 401, E_GET_OR_UPDATE_CART_FAILED.name), // TODO
                Consider(E, 402, E_GET_OR_UPDATE_CART_FAILED.name) // TODO
            ),
            points.mLoad(), scope
        ) {
            ie?.let {
                service.api.cart(token = getTokenAsBearer())
            } ?: run {
                service.api.cart(token = getTokenAsBearer(), ie!!)
            }
        }
    }

    /**
     * Address - https://polls.apiblueprint.org/address/input POST
     * @param ie - Address data
     * @param oe - The response data is written to the provided LiveData
     */
    fun address(
        ie: ReqAddress,
        oe: MutableSharedFlow<Result<ResAddress>>,
        scope: CoroutineScope
    ): Job {
        logd("fun address() check by text invoke")

        return service.send(
            oe, handler,
            setOf(
                Consider(Y, 200, E_SUCCESS.name),
                Consider(E, 0, E_CHECK_ADDRESS_FAILED.name),
            ),
            points.mLoad(), scope
        ) {
            service.api.address(ie)
        }
    }

    /**
     * Address - https://polls.apiblueprint.org/address/coordinate POST
     * @param ie - Address data
     * @param oe - The response data is written to the provided LiveData
     */
    fun address(
        ie: ReqCoordinate,
        oe: MutableSharedFlow<Result<ResAddressItem>>,
        scope: CoroutineScope
    ): Job {
        logd("fun address() check by coordinates invoke")

        return service.send(
            oe, handler,
            setOf(
                Consider(Y, 200, E_SUCCESS.name),
                Consider(E, 0, E_CHECK_ADDRESS_FAILED.name),
            ),
            points.mLoad(), scope
        ) {
            service.api.address(ie)
        }
    }

    /**
     * Order - https://polls.apiblueprint.org/orders/new POST
     * @param ie - Order data
     * @param oe - The response data is written to the provided LiveData
     */
    fun orderNew(
        ie: ReqOrder,
        oe: MutableSharedFlow<Result<ResOrder>>,
        scope: CoroutineScope
    ): Job {
        logd("fun orderNew() invoke")

        return service.send(
            oe, handler,
            setOf(
                Consider(Y, 201, E_SUCCESS.name),
                Consider(E, 0, E_ORDER_NEW_FAILED.name),
                Consider(E, 400, E_ORDER_NEW_FAILED.name),
                Consider(E, 401, E_ORDER_NEW_FAILED.name), // TODO
                Consider(E, 402, E_ORDER_NEW_FAILED.name) // TODO
            ),
            points.mLoad(), scope
        ) {
            service.api.orderNew(token = getTokenAsBearer(), ie)
        }
    }

    /**
     * Orders - https://polls.apiblueprint.org/orders? GET
     * @param oe - The response data is written to the provided LiveData
     * @param date - Date for header If-Modified-Since
     */
    fun getOrders(
        offset: Int = 0,
        limit: Int = 10,
        date: Date,
        oe: MutableSharedFlow<Result<ResOrders>>,
        scope: CoroutineScope
    ): Job {
        logd("fun getOrders() invoke")

        return service.send(
            oe, handler,
            setOf(
                Consider(Y, 200, E_SUCCESS.name),
                Consider(E, 0, E_GET_ORDERS_FAILED.name),
                Consider(E, 401, E_GET_ORDERS_FAILED.name), // TODO
                Consider(E, 402, E_GET_ORDERS_FAILED.name) // TODO
            ),
            points.mLoad(), scope
        ) {
            service.api.getOrders(offset, limit, getTokenAsBearer(), date)
        }
    }

    /**
     * Orders - https://polls.apiblueprint.org/orders? GET
     * @param oe - The response data is written to the provided LiveData
     * @param date - Date for header If-Modified-Since
     */
    fun getOrdersStatuses(
        date: Date,
        oe: MutableSharedFlow<Result<ResOrdersStatus>>,
        scope: CoroutineScope
    ): Job {
        logd("fun getOrdersStatus() invoke")

        return service.send(
            oe, handler,
            setOf(
                Consider(Y, 200, E_SUCCESS.name),
                Consider(E, 0, E_GET_ORDERS_STATUSES_FAILED.name),
            ),
            points.mLoad(), scope
        ) {
            service.api.getOrdersStatuses(date)
        }
    }

    /**
     * orderCancel - https://polls.apiblueprint.org/orders/cancel PUT
     * @param ie - Order data for cancel
     * @param oe - The response data is written to the provided LiveData
     */
    fun orderCancel(
        ie: Int,
        oe: MutableSharedFlow<Result<ResOrderCancel>>,
        scope: CoroutineScope
    ): Job {
        logd("fun getUpdateCart() invoke")

        return service.send(
            oe, handler,
            setOf(
                Consider(Y, 202, E_SUCCESS.name),
                Consider(E, 0, E_ORDER_CANCEL_FAILED.name),
                Consider(E, 400, E_ORDER_CANCEL_FAILED.name),
                Consider(E, 401, E_ORDER_CANCEL_FAILED.name), // TODO
                Consider(E, 402, E_ORDER_CANCEL_FAILED.name) // TODO
            ),
            points.mLoad(), scope
        ) {
            service.api.orderCancel(token = getTokenAsBearer(), ReqOrderCancel(orderId = ie))
        }
    }
}
