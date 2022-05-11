package ru.storozh.extensions

import android.os.Build
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

@Suppress("DEPRECATION")
fun TextView.setTextColorRes(@ColorRes colorRes: Int) = if (Build.VERSION.SDK_INT >= 23) {
    setTextColor(ContextCompat.getColor(context, colorRes))
} else {
    setTextColor(resources.getColor(colorRes))
}

fun TextView.setToggleTextColor(
    @ColorRes colorRes1: Int,
    @ColorRes colorRes2: Int,
    toggle: Boolean = true
) =
    if (toggle) setTextColorRes(colorRes1) else setTextColorRes(colorRes2)

fun TextView.setTextAndVisible(@StringRes stringRes: Int, visible: Boolean = false) {
    text = this.context.getString(stringRes)
    visible(visible)
}

fun TextView.enable(value: Boolean) {
    this.isEnabled = value
}