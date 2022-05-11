package ru.storozh.common.validation

import android.text.TextUtils
import android.util.Patterns

fun interface ValidString : (String) -> Boolean {
    companion object
}

val ValidString.Companion.isValidEmail
    get() = ValidString {
        !TextUtils.isEmpty(it) && Patterns.EMAIL_ADDRESS.matcher(it).matches()
    }
val ValidString.Companion.isNotEmpty
    get() = ValidString {
        !TextUtils.isEmpty(it)
    }
val ValidString.Companion.isEmpty
    get() = ValidString {
        TextUtils.isEmpty(it)
    }
val ValidString.Companion.hasDigits
    get() = ValidString {
        it.matches(".*\\d+.*".toRegex())
    }
val ValidString.Companion.isRUorEN
    get() = ValidString {
        it.matches("(^[a-zA-Z]*\$|^[а-яА-ЯЁё]*\$)".toRegex())
    }

fun ValidString.Companion.lengthBetween(start: Int, end: Int) =
    ValidString { it.length in start..end }