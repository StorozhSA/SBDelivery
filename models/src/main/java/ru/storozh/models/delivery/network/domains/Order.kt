package ru.storozh.models.delivery.network.domains

import com.fasterxml.jackson.annotation.JsonProperty

data class ReqOrder(
    @JsonProperty("address")
    val address: String = "",
    @JsonProperty("apartment")
    val apartment: String = "",
    @JsonProperty("comment")
    val comment: String = "",
    @JsonProperty("entrance")
    val entrance: Int = 0,
    @JsonProperty("floor")
    val floor: Int = 0,
    @JsonProperty("intercom")
    val intercom: String = ""
)

data class ResOrder(
    @JsonProperty("active")
    val active: Boolean = false,
    @JsonProperty("address")
    val address: String = "",
    @JsonProperty("completed")
    val completed: Boolean = false,
    @JsonProperty("createdAt")
    val createdAt: Long = 0,
    @JsonProperty("id")
    val id: String = "",
    @JsonProperty("items")
    val items: List<OrderItem> = listOf(),
    @JsonProperty("statusId")
    val statusId: Int = 0,
    @JsonProperty("total")
    val total: Int = 0,
    @JsonProperty("updatedAt")
    val updatedAt: Long = 0
)

data class OrderItem(
    @JsonProperty("amount")
    val amount: Int = 0,
    @JsonProperty("dishId")
    val dishId: String = "",
    @JsonProperty("image")
    val image: String = "",
    @JsonProperty("name")
    val name: String = "",
    @JsonProperty("price")
    val price: Int = 0
)

class ResOrders : ArrayList<ResOrdersItem>()

data class ResOrdersItem(
    @JsonProperty("active")
    val active: Boolean = false,
    @JsonProperty("address")
    val address: String = "",
    @JsonProperty("completed")
    val completed: Boolean = false,
    @JsonProperty("createdAt")
    val createdAt: Long = 0,
    @JsonProperty("id")
    val id: String = "",
    @JsonProperty("items")
    val items: List<OrderItem> = listOf(),
    @JsonProperty("statusId")
    val statusId: Int = 0,
    @JsonProperty("total")
    val total: Int = 0,
    @JsonProperty("updatedAt")
    val updatedAt: Long = 0
)

class ResOrdersStatus : ArrayList<ResOrdersStatusItem>()

data class ResOrdersStatusItem(
    @JsonProperty("active")
    val active: Boolean = false,
    @JsonProperty("cancelable")
    val cancelable: Boolean = false,
    @JsonProperty("createdAt")
    val createdAt: Long = 0,
    @JsonProperty("id")
    val id: Int = 0,
    @JsonProperty("name")
    val name: String = "",
    @JsonProperty("updatedAt")
    val updatedAt: Long = 0
)

data class ReqOrderCancel(
    @JsonProperty("orderId")
    val orderId: Int = 0
)

data class ResOrderCancel(
    @JsonProperty("active")
    val active: Boolean = false,
    @JsonProperty("address")
    val address: String = "",
    @JsonProperty("completed")
    val completed: Boolean = false,
    @JsonProperty("createdAt")
    val createdAt: Long = 0,
    @JsonProperty("id")
    val id: String = "",
    @JsonProperty("items")
    val items: List<OrderItem> = listOf(),
    @JsonProperty("statusId")
    val statusId: Int = 0,
    @JsonProperty("total")
    val total: Int = 0,
    @JsonProperty("updatedAt")
    val updatedAt: Long = 0
)
