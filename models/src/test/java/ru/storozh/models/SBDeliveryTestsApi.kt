package ru.storozh.models

import android.content.Context
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.storozh.common.network.RetrofitServiceImpl
import ru.storozh.models.delivery.network.DeliveryConnect
import ru.storozh.models.delivery.network.domains.*
import java.util.*

@Ignore
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SBDeliveryTestsApi {
    private val mockServerURL = "https://private-048b34-sbdelivery.apiary-mock.com/"

    @Mock
    private lateinit var mockContext: Context
    private val service = RetrofitServiceImpl(
        connect = DeliveryConnectForTest(),
        cache = null,
        authToken = null,
        interceptorsApp = setOf()
    )

    @Before
    fun method() {
        // `when`(mockContext.cacheDir).thenReturn(File.createTempFile("ssssss", "kkssss"))
    }

    @Test
    fun test_api_login_success() = runBlocking {
        val response = service.api.login(
            ReqLogin(
                email = "some.email@example.com",
                password = "SomePassword123"
            )
        )

        assertTrue(response.isSuccessful)
        assertEquals(201, response.code())
        assertEquals(
            ResLogin(
                id = "12345",
                firstName = "Иван",
                lastName = "Сидоров",
                email = "some.email@example.com",
                accessToken = "sometokenstring",
                refreshToken = "othertokenstring"
            ),
            response.body() ?: ResLogin()
        )
    }

    @Test
    fun test_api_register_success() = runBlocking {
        val response = service.api.register(
            ReqRegister(
                email = "some.email@example.com",
                password = "SomePassword123",
                firstName = "Иван",
                lastName = "Сидоров"
            )
        )

        assertTrue(response.isSuccessful)
        assertEquals(201, response.code())
        assertEquals(
            ResRegister(
                id = "12345",
                firstName = "Иван",
                lastName = "Сидоров",
                email = "some.email@example.com",
                accessToken = "sometokenstring",
                refreshToken = "othertokenstring"
            ),
            response.body() ?: ResRegister()
        )
    }

    @Test
    fun test_api_recovery_email_success() = runBlocking {
        val response = service.api.recovery(
            ReqRecoveryEmail(email = "some.email@example.com")
        )

        assertTrue(response.isSuccessful)
        assertEquals(201, response.code())
    }

    @Test
    fun test_api_recovery_code_success() = runBlocking {
        val response = service.api.recovery(
            ReqRecoveryCode(code = "1234", email = "some.email@example.com")
        )

        assertTrue(response.isSuccessful)
        assertEquals(201, response.code())
    }

    @Test
    fun test_api_recovery_pass_success() = runBlocking {
        val response = service.api.recovery(
            ReqRecoveryPassword(
                code = "1234",
                email = "some.email@example.com",
                password = "NewPassword123"
            )
        )

        assertTrue(response.isSuccessful)
        assertEquals(201, response.code())
    }

    @Test
    fun test_api_refresh_success() = runBlocking {
        val response = service.api.refresh(ReqRecoveryToken(refreshToken = "othertokenstring"))
        assertTrue(response.isSuccessful)
        assertEquals(201, response.code())
        assertEquals(
            ResRecoveryToken(accessToken = "sometokenstring"),
            response.body() ?: ResRecoveryToken()
        )
    }

    @Test
    fun test_api_profile_get_success() = runBlocking {
        val response = service.api.profile("1234567890")
        assertTrue(response.isSuccessful)
        assertEquals(200, response.code())
        assertEquals(
            ResUserProfile(
                id = "12345",
                firstName = "Иван",
                lastName = "Сидоров",
                email = "some.email@example.com"
            ),
            response.body() ?: ResUserProfile()
        )
    }

    @Test
    fun test_api_profile_edit_success() = runBlocking {
        val response = service.api.profile(
            ReqUserProfile(
                firstName = "Иван",
                lastName = "Сидоров",
                email = "some.email@example.com"
            ),
            "1234567890"
        )
        assertTrue(response.isSuccessful)
        assertEquals(202, response.code())
        assertEquals(
            ResUserProfile(
                id = "12345",
                firstName = "Иван",
                lastName = "Сидоров",
                email = "some.email@example.com"
            ),
            response.body() ?: ResUserProfile()
        )
    }

    @Test
    fun test_api_password_change_success() = runBlocking {
        val response = service.api.changePassword(
            ReqNewPassword(newPassword = "NewPassword123", oldPassword = "SomePassword123"),
            "1234567890"
        )
        assertTrue(response.isSuccessful)
        assertEquals(202, response.code())
    }

    @Test
    fun test_api_favorite_get_success() = runBlocking {
        val response = service.api.favorite(token = "1234567890", date = Date())
        assertTrue(response.isSuccessful)
        assertEquals(200, response.code())
        assertEquals(
            ResFavorite().apply {
                add(
                    ResFavoriteItem(
                        dishId = "12345", favorite = true, updatedAt = 1569235583286
                    )
                )
            },
            response.body() ?: ResFavorite()
        )
    }

    @Test
    fun test_api_favorite_put_success() = runBlocking {
        val response = service.api.favorite(
            body = ReqFavorite().apply {
                add(
                    ReqFavoriteItem(
                        dishId = "12345",
                        favorite = true
                    )
                )
            },
            token = "1234567890"
        )
        assertTrue(response.isSuccessful)
        assertEquals(202, response.code())
    }

    @Test
    fun test_api_recommended_get_success() = runBlocking {
        val response = service.api.getRecommended()
        assertTrue(response.isSuccessful)
        assertEquals(200, response.code())
        assertEquals(
            setOf(123, 124, 125), response.body() ?: setOf(ResFavoriteItem())
        )
    }

    @Test
    fun test_api_categories_get_success() = runBlocking {
        val response = service.api.getCategories(date = Date())
        assertTrue(response.isSuccessful)
        assertEquals(200, response.code())
        assertEquals(
            ResDishCategories().apply {
                add(
                    ResCategoryItem(
                        id = "12345",
                        name = "Акции",
                        order = 1,
                        icon = "http://someurl.com/something",
                        parent = "12344",
                        active = true,
                        createdAt = 1569235583286,
                        updatedAt = 1569235583286
                    )
                )
            },
            response.body() ?: ResDishCategories()
        )
    }

    @Test
    fun test_api_dishes_get_success() = runBlocking {
        val response = service.api.getDishes(date = Date())
        assertTrue(response.isSuccessful)
        assertEquals(200, response.code())
        assertEquals(
            ResDishes().apply {
                add(
                    ResDishItem(
                        id = "12345",
                        name = "Пицца Маргарита",
                        description = "Самая популярная пицца в нашем магазине!",
                        image = "http://someurl.com/something",
                        oldPrice = 780,
                        price = 680,
                        rating = 4.8,
                        commentsCount = 0,
                        likes = 128,
                        category = "12345",
                        active = true,
                        createdAt = 1569235583286,
                        updatedAt = 1569235583286
                    )
                )
            },
            response.body() ?: ResDishes()
        )
    }

    @Test
    fun test_api_reviews_get_success() = runBlocking {
        val response = service.api.getReviews(dishId = "12345")
        assertTrue(response.isSuccessful)
        assertEquals(200, response.code())
        assertEquals(
            ResReviews().apply {
                add(
                    ResReviewsItem(
                        id = "12345",
                        author = "Екатерина",
                        date = "",
                        rating = 4,
                        text = "Отличная пицца!",
                        active = true,
                        createdAt = 1569235583286,
                        updatedAt = 1569235583286
                    )
                )
            },
            response.body() ?: ResReviews()
        )
    }

    @Test
    fun test_api_reviews_add_success() = runBlocking {
        val response = service.api.addReview(
            dishId = "5ed636e6a91fec28b4fe3d21",
            token = "123456789",
            body = ReqReview(4, "Отличная пицца!")
        )
        assertTrue(response.isSuccessful)
        assertEquals(201, response.code())
    }

    @Test
    fun test_api_cart_get_success() = runBlocking {
        val response = service.api.cart(token = "1234567890")
        assertTrue(response.isSuccessful)
        assertEquals(200, response.code())
        assertEquals(
            ResCart(
                promocode = "PROMO",
                promotext = "Скидка 100 рублей!",
                total = 1960,
                items = listOf(CartItem(id = "12345", amount = 1, price = 680))
            ),
            response.body() ?: ResCart()
        )
    }

    @Test
    fun test_api_cart_put_success() = runBlocking {
        val response = service.api.cart(
            token = "1234567890",
            cart = ReqCart(
                promocode = "PROMO",
                items = listOf(CartItem(id = "12345", amount = 1))
            )
        )
        assertTrue(response.isSuccessful)
        assertEquals(202, response.code())
        assertEquals(
            ResCart(
                promocode = "PROMO",
                promotext = "Скидка 100 рублей!",
                total = 1960,
                items = listOf(CartItem(id = "12345", amount = 1, price = 680))
            ),
            response.body() ?: ResCart()
        )
    }

    @Test
    fun test_api_address_check_success() = runBlocking {
        val response = service.api.address(
            ReqAddress(
                address = "Москва, ул. Тверская, 5"
            )
        )

        assertTrue(response.isSuccessful)
        assertEquals(200, response.code())
        assertEquals(
            ResAddress().apply {
                add(
                    ResAddressItem(
                        city = "Москва",
                        street = "Тверская",
                        house = "5"
                    )
                )
            },
            response.body() ?: ResAddress()
        )
    }

    @Test
    fun test_api_address_check_coordinates_success() = runBlocking {
        val response = service.api.address(
            ReqCoordinate(
                lat = 55.757692,
                lon = 37.612067
            )
        )

        assertTrue(response.isSuccessful)
        assertEquals(200, response.code())
        assertEquals(
            ResAddressItem(
                city = "Москва",
                street = "Тверская",
                house = "5"
            ),
            response.body() ?: ResAddress()
        )
    }

    @Test
    fun test_api_order_new_success() = runBlocking {
        val response = service.api.orderNew(
            token = "1234567890",
            ReqOrder(
                address = "г. Москва, ул. Тверская, д. 5",
                entrance = 2,
                floor = 3,
                apartment = "25",
                intercom = "25",
                comment = "Постучать"
            )
        )

        assertTrue(response.isSuccessful)
        assertEquals(201, response.code())
        assertEquals(
            ResOrder(
                id = "12345",
                total = 1130,
                address = "г. Москва, ул. Тверская, д. 7",
                statusId = 1,
                active = true,
                completed = false,
                createdAt = 1569235583286,
                updatedAt = 1569235583286,
                items = listOf(
                    OrderItem(
                        name = "Пицца Маргарита",
                        image = "http://someurl.com/something",
                        amount = 1,
                        price = 1280,
                        dishId = "12345"
                    )
                )
            ),
            response.body() ?: ResOrder()
        )
    }

    @Test
    fun test_api_orders_get_success() = runBlocking {
        val response = service.api.getOrders(token = "1234567890", date = Date())
        assertTrue(response.isSuccessful)
        assertEquals(200, response.code())
        assertEquals(
            ResOrders().apply {
                add(
                    ResOrdersItem(
                        id = "12345",
                        total = 1130,
                        address = "г. Москва, ул. Тверская, д. 7",
                        statusId = 1,
                        active = true,
                        completed = false,
                        createdAt = 1569235583286,
                        updatedAt = 1569235583286,
                        items = listOf(
                            OrderItem(
                                name = "Пицца Маргарита",
                                image = "http://someurl.com/something",
                                amount = 1,
                                price = 1280,
                                dishId = "12345"
                            )
                        )
                    )
                )
            },
            response.body() ?: ResOrders()
        )
    }

    @Test
    fun test_api_orders_statuses_get_success() = runBlocking {
        val response = service.api.getOrdersStatuses(date = Date())
        assertTrue(response.isSuccessful)
        assertEquals(200, response.code())
        assertEquals(
            ResOrdersStatus().apply {
                add(
                    ResOrdersStatusItem(
                        id = 1,
                        name = "В обработке",
                        cancelable = true,
                        active = true,
                        createdAt = 1569235583286,
                        updatedAt = 1569235583286
                    )
                )
            },
            response.body() ?: ResOrdersStatus()
        )
    }

    @Test
    fun test_api_order_cancel_put_success() = runBlocking {
        val response = service.api.orderCancel(
            token = "1234567890",
            order = ReqOrderCancel(
                orderId = 12345
            )
        )
        assertTrue(response.isSuccessful)
        assertEquals(202, response.code())
        assertEquals(
            ResOrderCancel(
                id = "12345",
                total = 1130,
                address = "г. Москва, ул. Тверская, д. 7",
                statusId = 1,
                active = true,
                completed = false,
                createdAt = 1569235583286,
                updatedAt = 1569235583286,
                items = listOf(
                    OrderItem(
                        name = "Пицца Маргарита",
                        image = "http://someurl.com/something",
                        amount = 1,
                        price = 1280,
                        dishId = "12345"
                    )
                )
            ),
            response.body() ?: ResOrderCancel()
        )
    }

    //region
    inner class DeliveryConnectForTest : DeliveryConnect() {
        override val baseUrl: String
            get() = mockServerURL
    }
    //endregion
}
