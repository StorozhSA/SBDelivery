package ru.storozh.extensions

import android.view.View

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.visible(status: Boolean) {
    visibility = if (status) {
        View.VISIBLE
    } else {
        View.INVISIBLE
    }
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}