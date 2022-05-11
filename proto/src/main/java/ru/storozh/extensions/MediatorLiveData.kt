package ru.storozh.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

fun <T> MediatorLiveData<T>.update(upd: (currentState: T) -> T) {
    val updatedState: T = upd(this.value!!)
    this.value = updatedState
}

/***
 * функция принимает источник данных и лямбда выражение обрабатывающее поступающие данные источника
 * лямбда принимает новые данные и текущее состояние ViewModel в качестве аргументов,
 * изменяет его и возвращает модифицированное состояние, которое устанавливается как текущее
 *
 * Пример использования:
 * subscribeOnDataSource(getDataFromDataBaseOrNet()) { info, state ->
 *      info?.let {
 *          state.copy(
 *              isBookmark = it.isBookmark,
 *              isLike = it.isLike
 *          )
 *      }
 *  }
 */
fun <S, T> MediatorLiveData<T>.subscribeStateToDataSource(
    source: LiveData<S>,
    onChanged: (newValue: S, currentState: T) -> T
) {
    this.addSource(source) {
        this.value = onChanged(it, this.value!!) ?: return@addSource
    }
}