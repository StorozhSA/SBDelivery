package ru.skillbranch.sbdelivery

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import ru.storozh.common.AppSharedPreferences
import ru.storozh.extensions.TAG
import ru.storozh.extensions.logd
import ru.storozh.extensions.manage

@Suppress("PrivatePropertyName")
class AppSharedPreferencesHelper(context: Context) : AppSharedPreferences {

    override val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(
            context.packageName,
            Context.MODE_PRIVATE
        )
    }

    override val changeListenerCompanion: MutableSet<(key: String, value: Any) -> Unit> =
        mutableSetOf()

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, k ->
        logd("invoke OnSharedPreferenceChangeListener for key = $k")

        val value = when (prefs.all[k]) {
            is Int -> prefs.getInt(k, 0)
            is Long -> prefs.getLong(k, 0)
            is Float -> prefs.getFloat(k, 0f)
            is String -> prefs.getString(k, "")
            is Boolean -> prefs.getBoolean(k, false)
            else -> throw IllegalArgumentException("This type cannot be saved into Preferences")
        }

        changeListenerCompanion.forEach {
            it.invoke(k, value!!)
        }
    }

    // Version control of settings
    private var oldVersion: Int by prefs.manage(PREFERENCES_VERSION)

    // Use RAM for database (Read only for this application)
    private val dbIsRAM: Boolean by prefs.manage(false)

    // User registration status
    var userIsRegistered: Boolean by prefs.manage(false)

    // User auth status
    var userIsAuth: Boolean by prefs.manage(false)

    // Token of refresh access token
    var refreshToken: String by prefs.manage("")

    // Access token
    var accessToken: String by prefs.manage("")

    // Access token
    var userId: String by prefs.manage("0")

    // userFirstName
    var userFirstName: String by prefs.manage("")

    // userLastName
    var userLastName: String by prefs.manage("")

    // userEmail
    var userEmail: String by prefs.manage("")

    // lastModDateDishes
    var lastModDateDishes: Long by prefs.manage(0)

    // lastModDateCategory
    var lastModDateCategory: Long by prefs.manage(0)

    init {
        if (oldVersion < PREFERENCES_VERSION) migratePreferences()

        // Initializing other settings
        logd("Inited shared property dbIsRAM = $dbIsRAM")
        logd("Inited shared property userIsRegistered = $userIsRegistered")
        logd("Inited shared property userIsAuth = $userIsAuth")
        logd("Inited shared property refreshToken = $refreshToken")
        logd("Inited shared property accessToken = $accessToken")
        logd("Inited shared property userId = $userId")
        logd("Inited shared property userFirstName = $userFirstName")
        logd("Inited shared property userLastName = $userLastName")
        logd("Inited shared property userEmail = $userEmail")
        logd("Inited shared property lastModDateDishes = $lastModDateDishes")
        logd("Inited shared property lastModDateCategory = $lastModDateCategory")
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun migratePreferences() {
        Log.i(TAG, "migratePreferences() invoke. Settings migration process started.")
        // //////// Migration procedures ///////////
        oldVersion = PREFERENCES_VERSION
        Log.i(TAG, "migratePreferences() invoke. Settings migration process end.")
    }

    companion object {
        const val PREFERENCES_VERSION = 1
    }
}
