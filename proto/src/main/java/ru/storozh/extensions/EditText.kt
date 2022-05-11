package ru.storozh.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

fun EditText.watch(afterTextChanged: Editable.() -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            s.afterTextChanged()
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit
    })
}

fun EditText.updateText(text: String) {
    if (this.text.toString() != text)
        this.setText(text)
}

fun EditText.enable() {
    with(this) {
        isLongClickable = true
        isFocusable = true
        isCursorVisible = true
        requestFocus()
    }
}

fun EditText.disable() {
    with(this) {
        isLongClickable = false
        isFocusable = false
        isCursorVisible = false
        clearFocus()
    }
}


