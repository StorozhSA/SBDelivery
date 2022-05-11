package ru.storozh.models.delivery.network.domains

import com.fasterxml.jackson.annotation.JsonProperty

class ResFavorite : ArrayList<ResFavoriteItem>()

data class ResFavoriteItem(
    @JsonProperty("dishId")
    val dishId: String = "",
    @JsonProperty("favorite")
    val favorite: Boolean = false,
    @JsonProperty("updatedAt")
    val updatedAt: Long = 0
)

class ReqFavorite : ArrayList<ReqFavoriteItem>()

data class ReqFavoriteItem(
    @JsonProperty("dishId")
    val dishId: String = "",
    @JsonProperty("favorite")
    val favorite: Boolean = false
)
