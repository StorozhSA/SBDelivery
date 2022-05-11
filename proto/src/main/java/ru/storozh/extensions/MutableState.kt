package ru.storozh.extensions

import androidx.compose.runtime.MutableState

fun <T> MutableState<T>.update(upd: (currentState: T) -> T) {
    val updatedState: T = upd(this.value)
    this.value = updatedState
}