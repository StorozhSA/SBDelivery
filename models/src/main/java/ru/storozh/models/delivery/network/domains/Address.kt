package ru.storozh.models.delivery.network.domains

import com.fasterxml.jackson.annotation.JsonProperty

data class ReqAddress(
    @JsonProperty("address")
    val address: String = ""
)

class ResAddress : ArrayList<ResAddressItem>()

data class ResAddressItem(
    @JsonProperty("city")
    val city: String = "",
    @JsonProperty("house")
    val house: String = "",
    @JsonProperty("street")
    val street: String = ""
)

data class ReqCoordinate(
    @JsonProperty("lat")
    val lat: Double = 0.0,
    @JsonProperty("lon")
    val lon: Double = 0.0
)
