package ru.storozh.extensions

fun Int.decZero(): Int {
    return if (this.dec() > 0) this.dec() else 0
}