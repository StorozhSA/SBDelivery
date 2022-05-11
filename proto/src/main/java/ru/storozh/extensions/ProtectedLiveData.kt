package ru.storozh.extensions

import ru.storozh.extensions.common.ProtectedLiveData

@Synchronized
fun <T> ProtectedLiveData<T>.update(upd: (currentState: T) -> T) {
    val updatedState: T = upd(this.value!!)
    this.setValue(updatedState)
}

@Synchronized
fun <T> ProtectedLiveData<T>.update(protect: String, upd: (currentState: T) -> T) {
    val updatedState: T = upd(this.value!!)
    this.setValue(updatedState, protect)
}