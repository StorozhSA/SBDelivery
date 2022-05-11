package ru.storozh.models.delivery.network

import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import ru.storozh.common.AppSharedPreferences
import ru.storozh.common.network.RetrofitAuthenticator
import ru.storozh.common.network.RetrofitService
import ru.storozh.extensions.logd
import ru.storozh.extensions.manage
import ru.storozh.models.delivery.network.domains.ReqRecoveryToken

class DeliveryAuthenticator(pref: AppSharedPreferences) :
    RetrofitAuthenticator<DeliveryConnect, DeliveryAPI> {
    private var refreshToken: String by pref.prefs.manage("")
    private var accessToken: String by pref.prefs.manage("")
    private var service: RetrofitService<DeliveryConnect, DeliveryAPI>? = null

    override fun authenticate(route: Route?, response: Response): Request? {
        // This is a synchronous call
        if (response.code == 401 && service != null) {
            val res = service!!.api.refreshSynchronous(ReqRecoveryToken(refreshToken)).execute()
            if (res.isSuccessful) {
                accessToken = res.body()!!.accessToken
                return response.request
                    .newBuilder()
                    .header("Authorization", "Bearer $accessToken")
                    .build()
            }
        }
        return null
    }

    override fun setService(_service: RetrofitService<DeliveryConnect, DeliveryAPI>) {
        logd("Service for DeliveryAuthenticator injected")
        service = _service
    }
}
