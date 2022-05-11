package ru.storozh.extensions

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputEditText

fun TextInputEditText.watch(afterTextChanged: Editable.() -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            s.afterTextChanged()
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit
    })
}

fun TextInputEditText.withLiveData(owner: LifecycleOwner, liveData: MutableLiveData<String?>) {
    watch { liveData.value = text.toString() }
    liveData.observe(owner, Observer { value ->
        if (value == text.toString()) {
            return@Observer
        }
        setText(value)
    })
}