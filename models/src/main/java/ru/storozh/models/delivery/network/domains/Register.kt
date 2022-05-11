package ru.storozh.models.delivery.network.domains

import com.fasterxml.jackson.annotation.JsonProperty

data class ReqRegister(
    @JsonProperty("email")
    val email: String = "",
    @JsonProperty("firstName")
    val firstName: String = "",
    @JsonProperty("lastName")
    val lastName: String = "",
    @JsonProperty("password")
    val password: String = ""
)

data class ResRegister(
    @JsonProperty("accessToken")
    val accessToken: String = "",
    @JsonProperty("email")
    val email: String = "",
    @JsonProperty("firstName")
    val firstName: String = "",
    @JsonProperty("id")
    val id: String = "",
    @JsonProperty("lastName")
    val lastName: String = "",
    @JsonProperty("refreshToken")
    val refreshToken: String = ""
)
