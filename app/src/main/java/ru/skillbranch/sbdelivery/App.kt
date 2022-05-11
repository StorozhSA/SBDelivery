package ru.skillbranch.sbdelivery

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import okhttp3.Headers.Companion.headersOf
import retrofit2.Response
import ru.skillbranch.sbdelivery.data.EndPointsImpl
import ru.skillbranch.sbdelivery.data.RepoDataBase
import ru.skillbranch.sbdelivery.data.RepoNetwork
import ru.storozh.common.AppSharedPreferences
import ru.storozh.common.EndPoints
import ru.storozh.common.Notify
import ru.storozh.common.network.FOR_DATA
import ru.storozh.common.network.NetworkMonitor
import ru.storozh.common.network.Result
import ru.storozh.extensions.findStringByNameId
import ru.storozh.extensions.logd
import ru.storozh.extensions.randomString
import ru.storozh.models.delivery.network.domains.*
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@HiltAndroidApp
class App : Application(), LifecycleObserver {
    @Inject
    lateinit var pref: AppSharedPreferences

    @Inject
    lateinit var point: EndPoints

    @Inject
    lateinit var database: RepoDataBase

    @Inject
    lateinit var network: RepoNetwork

    @Inject
    lateinit var nm: NetworkMonitor

    private lateinit var appScope: CoroutineScope
    private val lfOwner by lazy { ProcessLifecycleOwner.get() }
    private val points by lazy { point as EndPointsImpl }
    private val prefs by lazy { pref as AppSharedPreferencesHelper }

    override fun onCreate() {
        super.onCreate()

        // Init lifecycle observe
        lfOwner.lifecycle.addObserver(this)

        // Init bridge between SharedPreferences and Flow
        prefs.changeListenerCompanion.apply {
            add { k, v ->
                logd("add OnSharedPreferenceChangeListener for key = $k value=$v")
                when (k) {
                    "userIsRegistered" -> points.sharedPref.mUserIsRegistered(v as Boolean)
                    "userIsAuth" -> points.sharedPref.mUserIsAuth(v as Boolean)
                }
            }
        }
    }

    @ExperimentalTime
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        logd("APP ON_START")

        // Init app scope
        appScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

        // Registration event (over shared preferences change listener)
        appScope.launch(Dispatchers.Main) {
            points.sharedPref.iUserIsRegistered().collect {
                points.notify(Notify.TextMessage(getString(R.string.is_registered)))
            }
        }

        // Login event (over shared preferences change listener)
        appScope.launch(Dispatchers.Main) {
            points.sharedPref.iUserIsAuth().collect {
                // vm.updStateAuth(it)
                points.notify(
                    Notify.TextMessage(
                        if (it) getString(R.string.you_login) else getString(
                            R.string.you_logout
                        )
                    )
                )
            }
        }

        // Observer init, update categories and dishes if connected
        nm.isConnected.observe(lfOwner) {
            if (it) {
                // Clean cache recommendation dishes
                database.cleanRecommendationDishes()
                // Update
                //  loadAndUpdateDishes(appScope)
                //  loadAndUpdateCategories(appScope)
                //   checkRecommendationDishes(appScope)
                //  checkFavoriteDishesChangesInDatabase(appScope)
            }
        }

        appScope.launch(Dispatchers.Main) {
            // Notify messages event channel init
            points.iNotifyFlow().collect {
                logd("message: ${it.message}")
                when (it) {
                    is Notify.LinkMessage -> Toast.makeText(
                        applicationContext,
                        applicationContext.findStringByNameId(it.message),
                        Toast.LENGTH_LONG
                    ).show()
                    is Notify.TextMessage -> Toast.makeText(
                        applicationContext,
                        it.message,
                        Toast.LENGTH_LONG
                    ).show()
                    is Notify.ErrorMessage -> Toast.makeText(
                        applicationContext,
                        applicationContext.findStringByNameId(it.message),
                        Toast.LENGTH_LONG
                    ).show()
                    is Notify.ActionMessage -> {
                        logd("received Notify.ActionMessage")
                    }
                }
            }
        }

        appScope.launch(Dispatchers.Default) {
            // Наблюдаем за REST регистрации пользователя (RepoNetwork.register()).
            // Пишем ответ в базу и в настройки.
            points.immutableResRegisterFlow().collect {
                logd("Invoke observer points.immutableResRegister() in App")
                if (it is Result.Success.Value) {
                    database.saveProfileIsRegister(it)
                    prefs.apply {
                        userIsRegistered = true
                        // userIsAuth = true
                        accessToken = it.payload.accessToken
                        refreshToken = it.payload.refreshToken
                        userId = it.payload.id
                        userFirstName = it.payload.firstName
                        userLastName = it.payload.lastName
                        userEmail = it.payload.email
                    }
                }
            }
        }

        appScope.launch(Dispatchers.Default) {
            // Наблюдаем за REST входа пользователя (RepoNetwork.login()).
            // Пишем ответ в базу и в настройки.
            points.immutableResLoginFlow().collectLatest {
                logd("Invoke observer points.immutableResLogin() in App")
                if (it is Result.Success.Value) {
                    database.saveProfileIsLogin(it)
                    prefs.apply {
                        userIsRegistered = true
                        userIsAuth = true
                        accessToken = it.payload.accessToken
                        refreshToken = it.payload.refreshToken
                        userId = it.payload.id
                        userFirstName = it.payload.firstName
                        userLastName = it.payload.lastName
                        userEmail = it.payload.email
                    }
                }
            }
        }

        //
        appScope.launch(Dispatchers.IO) {
            // Timer for check last modified date of dishes and categories
            tickerFlow(Duration.seconds(150), Duration.seconds(150)).collect {
                logd("Check and update  last modified date of dishes and categories")
                prefs.lastModDateDishes = database.getLastModDateDishes()
                prefs.lastModDateCategory = database.getLastModDateCategory()
            }
        }

        appScope.launch(Dispatchers.IO) {
            // Наблюдаем за REST редактирования профиля пользователя (RepoNetwork.profile()).
            // Пишем ответ в базу и в настройки.
            points.immutableResUserProfileFlow().collect {
                logd("Invoke observer points.immutableResUserProfile()")
                if (it is Result.Success.Value) {
                    database.saveProfileIsEdit(it)
                    prefs.apply {
                        userId = it.payload.id
                        userFirstName = it.payload.firstName
                        userLastName = it.payload.lastName
                        userEmail = it.payload.email
                    }
                }
            }
        }

        appScope.launch(Dispatchers.IO) {
            // Наблюдаем за REST обновления токена(RepoNetwork.refresh()).
            // Пишем ответ в базу и в настройки.
            points.immutableResRecoveryTokenFlow().collect {
                logd("Invoke observer points.immutableResRecoveryToken()")
                if (it is Result.Success.Value) {
                    database.saveAccessToken(it)
                    prefs.accessToken = it.payload.accessToken
                }
            }
        }

        appScope.launch(Dispatchers.IO) {
            // Наблюдаем за REST рекомендуемых бдюд (RepoNetwork.getRecommended()).
            // Пишем ответ в базу.
            points.immutableResRecommendationFlow().collect {
                logd("Invoke observer points.immutableResRecommendationFlow()")
                if (it is Result.Success.Value<Set<String>>) {
                    database.saveRecommendationDishes(it)
                }
            }
        }

        appScope.launch(Dispatchers.Default) {
            // Наблюдаем за REST избранных бдюд (RepoNetwork.favorites()).
            // Это ответ при оправке своего списка избранного.
            points.immutableResFavoriteUnitFlow().collectLatest {
                logd("Invoke observer points.mutableResFavoriteUnitFlow()")
                when (it) {
                    is Result.Success.Empty -> {
                        logd("----Success")
                    }
                    is Result.Success.Value -> {
                        logd("----Value")
                    }
                    is Result.Failure -> {
                        logd("----Failure")
                    }
                }
            }
        }

        val favoritesLocal: MutableList<ResFavoriteItem> = mutableListOf()

        appScope.launch(Dispatchers.IO) {
            // Наблюдаем за REST избранных бдюд (RepoNetwork.favorites()).
            // Это ответ при запросе всего избранного.
            points.immutableResFavoriteFlow().collectLatest {
                logd("Invoke observer points.immutableResFavoriteFlow()")
                when (it) {
                    is Result.Success.Empty -> {
                        logd("Favorite list is empty")
                        analyzeFavorites(favoritesLocal, mutableListOf())
                    }
                    is Result.Success.Value -> {
                        logd("Favorites received: ${it.payload}")
                        analyzeFavorites(favoritesLocal, it.payload)
                    }
                    is Result.Failure -> {
                        logd("Error loading favorites list")
                    }
                }
            }
        }

        appScope.launch(Dispatchers.IO) {
            // Наблюдаем за изменениями в базе избранных бдюд (database.getAllFavoriteDishes()).
            points.immutableFavoriteDbFlow().collectLatest {
                logd("Invoke observer database.getAllFavoriteDishes()")

                // Готовим список локального избранного для сравнения с серверным
                favoritesLocal.clear()
                it.forEach { dish ->
                    favoritesLocal.add(
                        ResFavoriteItem(
                            dishId = dish.id,
                            favorite = true,
                            updatedAt = dish.updatedAt
                        )
                    )
                }

                // Запрашиваем все избранное на сервере
                /*if (prefs.userIsAuth) {
                    network.favorite(
                        oe = points.mutableResFavoriteFlow(),
                        offset = 0,
                        limit = 1000,
                        date = Date(0),
                        scope = appScope
                    )
                }*/
            }
        }

        appScope.launch(Dispatchers.IO) {
            // Наблюдаем за REST получения отзывов о блюде (RepoNetwork.getReviews()).
            // Это ответ при запросе отзывов по блюду.
            points.immutableResReviewsFlow().collectLatest {
                logd("Invoke observer points.immutableResReviewsFlow()")
                if (it is Result.Success.Value) {
                    database.saveReviews(it)
                }

                // TODO удалить при продакшн. Имитация ответа, так как тестовый сервис все время 304
                if (it is Result.Success.Empty) {

                    if (BuildConfig.DEBUG) {
                        val item1 = ResReviewsItem(
                            author = "Екатерина",
                            date = Date().time.toString(),
                            rating = 2,
                            text = "Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum",
                            createdAt = 0,
                            updatedAt = 0,
                            id = "1234567890".randomString(10),
                            active = true,
                        )
                        val item2 = ResReviewsItem(
                            author = "Петр",
                            date = Date().time.toString(),
                            rating = 2,
                            text = "Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum",
                            createdAt = 0,
                            updatedAt = 0,
                            id = "1234567890".randomString(10),
                            active = true
                        )
                        val payload = ResReviews().apply {
                            add(item1)
                            add(item2)
                        }

                        val response = Response.success(
                            payload,
                            headersOf(FOR_DATA, it.techHeaders[FOR_DATA] ?: "")
                        )

                        database.saveReviews(
                            Result.Success.Value<ResReviews>(
                                response = response,
                                ""
                            )
                        )
                    }
                }
            }
        }
    }

    private fun analyzeFavorites(
        favoritesLocal: MutableList<ResFavoriteItem>,
        favoritesServer: MutableList<ResFavoriteItem>,
    ) {
        // Готовим данные для update сервера и базы
        val setForChanges = mappingFavorites(favoritesLocal, favoritesServer)

        // Server update
        network.favorite(
            ReqFavorite().apply {
                addAll(
                    setForChanges.map {
                        ReqFavoriteItem(
                            dishId = it.dishId,
                            favorite = it.favorite
                        )
                    }
                )
            },
            points.mutableResFavoriteUnitFlow(),
            appScope
        )

        // Database update
        database.saveFavoriteDishes(setForChanges)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        logd("APP ON_STOP")
        appScope.cancel()
        nm.isConnected.removeObservers(lfOwner)
        // points.sharedPref.iUserIsRegistered().removeObservers(lfOwner)
        // points.sharedPref.iUserIsAuth().removeObservers(lfOwner)
    }

    // Load from net and save dish categories
    private fun loadAndUpdateCategories(
        scope: CoroutineScope
    ): Job {
        logd("invoke loadAndUpdateCategories")
        return scope.launch(Dispatchers.IO) {
            network.getCategoriesAll(prefs.lastModDateCategory, scope).buffer(100).collectLatest {
                database.saveCategories(it)
                preloadImages(
                    it
                        .filter { cat -> cat.icon?.isNotBlank() ?: false }
                        .map { cat -> cat.icon }.toList() as List<String>
                )
            }
        }
    }

    // Load from net and save dishes
    private fun loadAndUpdateDishes(
        scope: CoroutineScope
    ): Job {
        return scope.launch(Dispatchers.IO) {
            network.getDishesAll(prefs.lastModDateDishes, scope).buffer(500).collect {
                database.saveDishes(it)
                preloadImages(
                    it
                        .filter { dish -> dish.image?.isNotBlank() ?: false }
                        .map { dish -> dish.image }.toList() as List<String>
                )
            }
        }
    }

    // Preload images to disk cache
    private fun preloadImages(urls: List<String>) {
        urls.forEach {
            Glide.with(this.applicationContext)
                .load(it)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                .submit()
        }
    }

    // Check recommendation dishes
    private fun checkRecommendationDishes(scope: CoroutineScope): Job {
        return network.getRecommended(points.mutableResRecommendationFlow(), scope)
    }

    // Check favorite dishes in db
    private fun checkFavoriteDishesChangesInDatabase(scope: CoroutineScope): Job {
        return database.getAllFavoriteDishes(points.mutableFavoriteDbFlow(), scope)
    }

    @ExperimentalTime
    fun tickerFlow(period: Duration, initialDelay: Duration = Duration.ZERO) = flow {
        delay(initialDelay)
        while (true) {
            emit(Unit)
            delay(period)
        }
    }

    companion object {
        fun mappingFavorites(
            originalListFromLocal: List<ResFavoriteItem>,
            originalListFromServer: List<ResFavoriteItem>
        ): Set<ResFavoriteItem> {
            val operateSet = mutableSetOf<ResFavoriteItem>()
            operateSet.addAll(originalListFromLocal)
            operateSet.addAll(originalListFromServer)
            return operateSet.groupBy { it.dishId }.map {
                it.value.maxWithOrNull { o1, o2 -> if (o1.updatedAt > o2.updatedAt) 1 else -1 }
            }.filterNotNull().toSet()
        }
    }
}
