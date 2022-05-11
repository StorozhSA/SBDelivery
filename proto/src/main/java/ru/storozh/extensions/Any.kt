package ru.storozh.extensions

import android.util.Log


fun Any?.isNull(): Boolean {
    return this == null
}

fun Any?.isNotNull(): Boolean {
    return this != null
}

inline val <reified T> T.TAG: String
    get() = T::class.java.name

inline fun <reified T> T.logv(message: String) = Log.v(TAG, message)
inline fun <reified T> T.logi(message: String) = Log.i(TAG, message)
inline fun <reified T> T.logw(message: String) = Log.w(TAG, message)
inline fun <reified T> T.logd(message: String) = Log.d(TAG, message)
inline fun <reified T> T.loge(message: String) = Log.e(TAG, message)

fun isAllTrue(vararg elements: Boolean): Boolean {
    val elms = if (elements.isNotEmpty()) elements.toSet() else emptySet()
    return !elms.contains(false)
}
