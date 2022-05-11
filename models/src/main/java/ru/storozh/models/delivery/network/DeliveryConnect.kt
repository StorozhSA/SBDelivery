package ru.storozh.models.delivery.network

import ru.storozh.common.network.RetrofitConnect
import ru.storozh.models.BuildConfig

open class DeliveryConnect : RetrofitConnect<DeliveryAPI> {
    override val baseUrl: String = BuildConfig.SERVER

    // override val baseUrl: String = "https://sandbox.skill-branch.ru"
    override val api: Class<DeliveryAPI> = DeliveryAPI::class.java
}
