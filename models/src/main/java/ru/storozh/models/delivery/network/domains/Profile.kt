package ru.storozh.models.delivery.network.domains

import com.fasterxml.jackson.annotation.JsonProperty

data class ReqUserProfile(
    @JsonProperty("email")
    val email: String = "",
    @JsonProperty("firstName")
    val firstName: String = "",
    @JsonProperty("lastName")
    val lastName: String = ""
)

data class ResUserProfile(
    @JsonProperty("email")
    val email: String = "",
    @JsonProperty("firstName")
    val firstName: String = "",
    @JsonProperty("id")
    val id: String = "",
    @JsonProperty("lastName")
    val lastName: String = ""
)

data class ReqNewPassword(
    @JsonProperty("newPassword")
    val newPassword: String = "",
    @JsonProperty("oldPassword")
    val oldPassword: String = ""
)
