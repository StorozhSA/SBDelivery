package ru.storozh.models.delivery.network.domains

import com.fasterxml.jackson.annotation.JsonProperty

class ResReviews : ArrayList<ResReviewsItem>()

data class ResReviewsItem(
    @JsonProperty("active")
    val active: Boolean = false,
    @JsonProperty("author")
    val author: String = "",
    @JsonProperty("createdAt")
    val createdAt: Long = 0,
    @JsonProperty("date")
    val date: String = "",
    @JsonProperty("id")
    val id: String = "",
    @JsonProperty("rating")
    val rating: Int = 0,
    @JsonProperty("text")
    val text: String = "",
    @JsonProperty("updatedAt")
    val updatedAt: Long = 0
)

data class ReqReview(
    @JsonProperty("rating")
    val rating: Int = 0,
    @JsonProperty("text")
    val text: String = ""
)
