package ru.storozh.models.delivery.network.domains

import com.fasterxml.jackson.annotation.JsonProperty

data class ResCart(
    @JsonProperty("items")
    val items: List<CartItem> = listOf(),
    @JsonProperty("promocode")
    val promocode: String = "",
    @JsonProperty("promotext")
    val promotext: String = "",
    @JsonProperty("total")
    val total: Int = 0
)

data class CartItem(
    @JsonProperty("amount")
    val amount: Int = 0,
    @JsonProperty("id")
    val id: String = "",
    @JsonProperty("price")
    val price: Int = 0
)

data class ReqCart(
    @JsonProperty("items")
    val items: List<CartItem> = listOf(),
    @JsonProperty("promocode")
    val promocode: String = ""
)
