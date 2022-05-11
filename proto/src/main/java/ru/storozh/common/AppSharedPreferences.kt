package ru.storozh.common

import android.content.SharedPreferences

interface AppSharedPreferences {
    val prefs: SharedPreferences
    val changeListenerCompanion: MutableSet<(key: String, value: Any) -> Unit>
    fun migratePreferences()
}