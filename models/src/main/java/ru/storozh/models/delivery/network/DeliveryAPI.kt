package ru.storozh.models.delivery.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import ru.storozh.common.network.FOR_DATA
import ru.storozh.common.network.RetrofitAPI
import ru.storozh.models.delivery.network.domains.*
import java.util.*

interface DeliveryAPI : RetrofitAPI {

    @Headers("Content-Type: application/json")
    @POST("auth/login")
    suspend fun login(@Body body: ReqLogin): Response<ResLogin>

    @Headers("Content-Type: application/json")
    @POST("auth/register")
    suspend fun register(@Body body: ReqRegister): Response<ResRegister>

    @Headers("Content-Type: application/json")
    @POST("auth/recovery/email")
    suspend fun recovery(@Body body: ReqRecoveryEmail): Response<Unit>

    @Headers("Content-Type: application/json")
    @POST("auth/recovery/code")
    suspend fun recovery(@Body body: ReqRecoveryCode): Response<Unit>

    @Headers("Content-Type: application/json")
    @POST("auth/recovery/password")
    suspend fun recovery(@Body body: ReqRecoveryPassword): Response<Unit>

    @Headers("Content-Type: application/json")
    @POST("auth/refresh")
    suspend fun refresh(@Body body: ReqRecoveryToken): Response<ResRecoveryToken>

    @Headers("Content-Type: application/json")
    @POST("auth/refresh")
    fun refreshSynchronous(@Body body: ReqRecoveryToken): Call<ResRecoveryToken>

    @Headers("Content-Type: application/json")
    @GET("profile")
    suspend fun profile(@Header("Authorization") token: String): Response<ResUserProfile>

    @Headers("Content-Type: application/json")
    @GET("profile")
    suspend fun profile(): Response<ResUserProfile>

    @Headers("Content-Type: application/json")
    @PUT("profile")
    suspend fun profile(
        @Body body: ReqUserProfile,
        @Header("Authorization") token: String
    ): Response<ResUserProfile>

    @Headers("Content-Type: application/json")
    @PUT("profile/password")
    suspend fun changePassword(
        @Body body: ReqNewPassword,
        @Header("Authorization") token: String
    ): Response<Unit>

    @Headers("Content-Type: application/json")
    @GET("favorite")
    suspend fun favorite(
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 10,
        @Header("Authorization") token: String,
        @Header("If-Modified-Since") date: Date
    ): Response<ResFavorite>

    @Headers("Content-Type: application/json")
    @PUT("favorite")
    suspend fun favorite(
        @Body body: ReqFavorite,
        @Header("Authorization") token: String
    ): Response<Unit>

    @Headers("Content-Type: application/json")
    @GET("main/recommend")
    suspend fun getRecommended(): Response<Set<String>>

    @Headers("Content-Type: application/json")
    @GET("categories")
    suspend fun getCategories(
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 10,
        @Header("If-Modified-Since") date: Date
    ): Response<ResDishCategories>

    @Headers("Content-Type: application/json")
    @GET("dishes")
    suspend fun getDishes(
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 10,
        @Header("If-Modified-Since") date: Date
    ): Response<ResDishes>

    @Headers("Content-Type: application/json")
    @GET("reviews/{dishId}")
    suspend fun getReviews(
        @Path("dishId") dishId: String,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 10,
        @Header(FOR_DATA) fordata: String = dishId
    ): Response<ResReviews>

    @Headers("Content-Type: application/json")
    @POST("reviews/{dishId}")
    suspend fun addReview(
        @Path("dishId") dishId: String,
        @Body body: ReqReview,
        @Header("Authorization") token: String
    ): Response<Unit>

    @Headers("Content-Type: application/json")
    @GET("cart")
    suspend fun cart(@Header("Authorization") token: String): Response<ResCart>

    @Headers("Content-Type: application/json")
    @PUT("cart")
    suspend fun cart(@Header("Authorization") token: String, @Body cart: ReqCart): Response<ResCart>

    @Headers("Content-Type: application/json")
    @POST("address/input")
    suspend fun address(@Body body: ReqAddress): Response<ResAddress>

    @Headers("Content-Type: application/json")
    @POST("address/coordinates")
    suspend fun address(@Body body: ReqCoordinate): Response<ResAddressItem>

    @Headers("Content-Type: application/json")
    @POST("orders/new")
    suspend fun orderNew(
        @Header("Authorization") token: String,
        @Body body: ReqOrder
    ): Response<ResOrder>

    @Headers("Content-Type: application/json")
    @GET("orders")
    suspend fun getOrders(
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 10,
        @Header("Authorization") token: String,
        @Header("If-Modified-Since") date: Date
    ): Response<ResOrders>

    @Headers("Content-Type: application/json")
    @GET("orders/statuses")
    suspend fun getOrdersStatuses(@Header("If-Modified-Since") date: Date): Response<ResOrdersStatus>

    @Headers("Content-Type: application/json")
    @PUT("orders/cancel")
    suspend fun orderCancel(
        @Header("Authorization") token: String,
        @Body order: ReqOrderCancel
    ): Response<ResOrderCancel>
}
