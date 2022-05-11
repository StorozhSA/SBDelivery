package ru.skillbranch.sbdelivery.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.storozh.common.EndPoints
import ru.storozh.common.Notify
import ru.storozh.common.network.Result
import ru.storozh.models.delivery.database.domains.CartItemJoined
import ru.storozh.models.delivery.database.domains.DishV
import ru.storozh.models.delivery.network.domains.*

class EndPointsImpl : EndPoints {
    // Progress bar state
    private val mldLoad = MutableSharedFlow<Boolean>(
        replay = 0,
        extraBufferCapacity = 3,
        BufferOverflow.DROP_OLDEST
    )

    fun iLoad() = mldLoad.asSharedFlow()
    fun mLoad() = mldLoad

    // Notify events
    private val mldNotifyFlow = MutableSharedFlow<Notify>(
        replay = 0,
        extraBufferCapacity = 3,
        BufferOverflow.DROP_OLDEST
    )

    fun iNotifyFlow() = mldNotifyFlow.asSharedFlow()
    fun mNotifyFlow() = mldNotifyFlow
    fun mNotifyFlow(notify: Notify) {
        CoroutineScope(Dispatchers.Main).launch { mldNotifyFlow.emit(notify) }
    }

    // Result<ResLogin> from net
    private val mldLoginFlow = MutableSharedFlow<Result<ResLogin>>(replay = 0)
    fun immutableResLoginFlow() = mldLoginFlow.asSharedFlow()
    fun mutableResLoginFlow() = mldLoginFlow

    // Result<ResRegister> from net
    private val mldRegisterFlow = MutableSharedFlow<Result<ResRegister>>(replay = 0)
    fun immutableResRegisterFlow() = mldRegisterFlow.asSharedFlow()
    fun mutableResRegisterFlow() = mldRegisterFlow

    // Result<ResUserProfile> from net
    private val mldProfileFlow = MutableSharedFlow<Result<ResUserProfile>>(replay = 0)
    fun immutableResUserProfileFlow() = mldProfileFlow.asSharedFlow()
    fun mutableResUserProfileFlow() = mldProfileFlow

    // Result<ResRefreshToken> from net
    private val mldResRecoveryTokenFlow = MutableSharedFlow<Result<ResRecoveryToken>>()
    fun immutableResRecoveryTokenFlow() = mldResRecoveryTokenFlow.asSharedFlow()
    fun mutableResRecoveryTokenFlow() = mldResRecoveryTokenFlow

    // Result<Unit> from net
    private val mldResChangePasswordFlow = MutableSharedFlow<Result<Unit>>(replay = 0)
    fun immutableResChangePasswordFlow() = mldResChangePasswordFlow.asSharedFlow()
    fun mutableResChangePasswordFlow() = mldResChangePasswordFlow

    // Result<Unit> from net/ Recovery password step 1
    private val mldRecovery1PasswordFlow = MutableSharedFlow<Result<Unit>>(replay = 0)
    fun immutableRecovery1PasswordFlow() = mldRecovery1PasswordFlow.asSharedFlow()
    fun mutableRecovery1PasswordFlow() = mldRecovery1PasswordFlow

    // Result<Unit> from net/ Recovery password step 2
    private val mldRecovery2PasswordFlow = MutableSharedFlow<Result<Unit>>(replay = 0)
    fun immutableRecovery2PasswordFlow() = mldRecovery2PasswordFlow.asSharedFlow()
    fun mutableRecovery2PasswordFlow() = mldRecovery2PasswordFlow

    // Result<Unit> from net/ Recovery password step 3
    private val mldRecovery3PasswordFlow = MutableSharedFlow<Result<Unit>>(replay = 0)
    fun immutableRecovery3PasswordFlow() = mldRecovery3PasswordFlow.asSharedFlow()
    fun mutableRecovery3PasswordFlow() = mldRecovery3PasswordFlow

    // Result<Set<Int>> from net
    private val mldResRecommendationFlow = MutableSharedFlow<Result<Set<String>>>()
    fun immutableResRecommendationFlow() = mldResRecommendationFlow.asSharedFlow()
    fun mutableResRecommendationFlow() = mldResRecommendationFlow

    // Result<Result<ResFavorite>> from net
    private val mldFavoriteFlow = MutableSharedFlow<Result<ResFavorite>>(replay = 0)
    fun immutableResFavoriteFlow() = mldFavoriteFlow.asSharedFlow()
    fun mutableResFavoriteFlow() = mldFavoriteFlow

    // <Result<Unit>> from net
    private val mldResFavoriteUnitFlow = MutableSharedFlow<Result<Unit>>(replay = 0)
    fun immutableResFavoriteUnitFlow() = mldResFavoriteUnitFlow.asSharedFlow()
    fun mutableResFavoriteUnitFlow() = mldResFavoriteUnitFlow

    // <List<DishV>> from db
    private val mldFavoriteDbFlow = MutableSharedFlow<List<DishV>>(replay = 1)
    fun immutableFavoriteDbFlow() = mldFavoriteDbFlow.asSharedFlow()
    fun mutableFavoriteDbFlow() = mldFavoriteDbFlow

    // Result<ResReviews>> from net
    private val mldResReviewsFlow = MutableSharedFlow<Result<ResReviews>>(replay = 0)
    fun immutableResReviewsFlow() = mldResReviewsFlow.asSharedFlow()
    fun mutableResReviewsFlow() = mldResReviewsFlow

    // <Result<Unit>> from net
    private val mldResAddReviewUnitFlow = MutableSharedFlow<Result<Unit>>(replay = 0)
    fun immutableResAddReviewUnitFlow() = mldResAddReviewUnitFlow.asSharedFlow()
    fun mutableResAddReviewUnitFlow() = mldResAddReviewUnitFlow

    // <List<CartItemJoined>> from db
    private val mldCartItemJoinedFlow = MutableSharedFlow<List<CartItemJoined>>(replay = 1)
    fun immutableCartItemJoinedFlow() = mldCartItemJoinedFlow.asSharedFlow()
    fun mutableCartItemJoinedFlow() = mldCartItemJoinedFlow

    // Data projection in SharedPreferences
    class SharedPreferences {

        // Status user registration
        private val userIsRegistered = MutableSharedFlow<Boolean>()
        fun iUserIsRegistered() = userIsRegistered.asSharedFlow()
        fun mUserIsRegistered() = userIsRegistered
        fun mUserIsRegistered(status: Boolean = false) {
            CoroutineScope(Dispatchers.Default).launch {
                userIsRegistered.emit(status)
            }
        }

        // Status user auth
        private val userIsAuth = MutableSharedFlow<Boolean>()
        fun iUserIsAuth() = userIsAuth.asSharedFlow()
        fun mUserIsAuth() = userIsAuth
        fun mUserIsAuth(status: Boolean = false) {
            CoroutineScope(Dispatchers.Default).launch {
                userIsAuth.emit(status)
            }
        }
    }

    val sharedPref = SharedPreferences()

    override fun notify(n: Notify) {
        mNotifyFlow(n)
    }
}
