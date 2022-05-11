package ru.storozh.common

import androidx.lifecycle.Observer
import java.util.concurrent.CopyOnWriteArraySet
import javax.annotation.concurrent.ThreadSafe

@ThreadSafe
open class Event<out T>(private val value: T) {
    private val taked = CopyOnWriteArraySet<Int>()

    fun getIfNotTaked(): T? {
        return if (taked.isEmpty()) {
            taked.add(0)
            value
        } else {
            null
        }
    }

    fun getIfNotTaked(code: Int): T? {
        return if (taked.contains(code)) {
            null
        } else {
            taked.add(code)
            value
        }
    }

    fun peek(): T = value
    fun isTaked(): Boolean = !taked.isEmpty()
    fun isTaked(code: Int): Boolean = taked.contains(code)
}

class EventObserverSingle<T>(private val observe: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getIfNotTaked()?.let { value -> observe(value) }
    }
}

class EventObserverSingleSelf<T>(private val code: Int, private val observe: (T) -> Unit) :
    Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getIfNotTaked(code)?.let { value -> observe(value) }
    }
}

