package ru.storozh.models.delivery.network.domains

import com.fasterxml.jackson.annotation.JsonProperty

data class ReqRecoveryEmail(
    @JsonProperty("email")
    val email: String = ""
)

data class ReqRecoveryCode(
    @JsonProperty("code")
    val code: String = "",
    @JsonProperty("email")
    val email: String = ""
)

data class ReqRecoveryPassword(
    @JsonProperty("code")
    val code: String = "",
    @JsonProperty("email")
    val email: String = "",
    @JsonProperty("password")
    val password: String = ""
)

data class ReqRecoveryToken(
    @JsonProperty("refreshToken")
    val refreshToken: String = ""
)

data class ResRecoveryToken(
    @JsonProperty("accessToken")
    val accessToken: String = ""
)
