package ru.storozh.extensions

import android.text.TextUtils
import android.util.Patterns

/**
 * Return Set of indices, occurrence of substring start.
 */
fun String?.indexesOf(substr: String, ignoreCase: Boolean = true): Set<Int> {
    if (substr.trim().isEmpty()) return emptySet()
    val result = mutableSetOf<Int>()
    var index = this?.indexOf(substr, 0, ignoreCase) ?: 0
    while (index >= 0) {
        result.add(index)
        index = this?.indexOf(substr, index + 1, ignoreCase) ?: -1
    }
    return result
}

fun String?.randomString(length: Int): String {
    var localLength = this?.length ?: 1
    if (length in 1 until localLength) localLength = length
    return (1..localLength).map { this?.random() }.joinToString("")
}

fun String?.randomString(): String {
    return (1..this?.length!!).map { this.random() }.joinToString("")
}

fun String.hasDigits(): Boolean {
    return this.matches(".*\\d+.*".toRegex())
}

fun String.isEmailValid(): Boolean {
    return !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}