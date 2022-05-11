package ru.storozh.extensions

import androidx.lifecycle.MutableLiveData

@Synchronized
fun <T> MutableLiveData<T>.update(upd: (currentState: T) -> T) {
    val updatedState: T = upd(this.value!!)
    this.value = updatedState
}