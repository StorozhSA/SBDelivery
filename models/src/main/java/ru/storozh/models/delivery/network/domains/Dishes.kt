package ru.storozh.models.delivery.network.domains

import com.fasterxml.jackson.annotation.JsonProperty

class ResDishCategories : ArrayList<ResCategoryItem>()

data class ResCategoryItem(
    @JsonProperty("categoryId")
    val id: String?,
    @JsonProperty("active")
    val active: Boolean?,
    @JsonProperty("icon")
    val icon: String?,
    @JsonProperty("name")
    val name: String?,
    @JsonProperty("order")
    val order: Int?,
    @JsonProperty("parent")
    val parent: String?,
    @JsonProperty("updatedAt")
    val updatedAt: Long?,
    @JsonProperty("createdAt")
    val createdAt: Long?
)

class ResDishes : ArrayList<ResDishItem>()

data class ResDishItem(
    @JsonProperty("active")
    val active: Boolean?,
    @JsonProperty("category")
    val category: String?,
    @JsonProperty("commentsCount")
    val commentsCount: Int?,
    @JsonProperty("createdAt")
    val createdAt: Long?,
    @JsonProperty("description")
    val description: String?,
    @JsonProperty("id")
    val id: String?,
    @JsonProperty("image")
    val image: String?,
    @JsonProperty("likes")
    val likes: Int?,
    @JsonProperty("name")
    val name: String?,
    @JsonProperty("oldPrice")
    val oldPrice: Int?,
    @JsonProperty("price")
    val price: Int?,
    @JsonProperty("rating")
    val rating: Double?,
    @JsonProperty("updatedAt")
    val updatedAt: Long?
)
