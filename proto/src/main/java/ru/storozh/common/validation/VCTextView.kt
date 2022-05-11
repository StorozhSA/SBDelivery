package ru.storozh.common.validation

import android.util.Log
import android.widget.TextView
import ru.storozh.extensions.visible
import java.util.*

class VCTextView(private var textView: TextView) {
    private val msgs: MutableMap<Int, Long> = mutableMapOf()
    fun updateProcessValidation(msgId: Int, op: Boolean) {
        if (op) msgs.remove(msgId) else msgs[msgId] = Date().time
        Log.d("TAG", "size = ${msgs.size}")
        msgs.maxByOrNull { it.value }
            ?.let {
                textView.text = textView.context.getString(it.key)
                textView.visible(msgs.isNotEmpty())
            } ?: run {
            textView.visible(msgs.isNotEmpty())
        }
    }

    fun hasErrors() = msgs.isNotEmpty()
}