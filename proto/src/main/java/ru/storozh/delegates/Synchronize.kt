@file:Suppress("RedundantGetter", "RedundantSetter")

package ru.storozh.delegates

import javax.annotation.concurrent.ThreadSafe
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@ThreadSafe
class Synchronize<T>(defaultValue: T) : ReadWriteProperty<Any, T> {
    private val lock = Object()

    @Volatile
    private var backingField: T = setAndGet(defaultValue)

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        synchronized(lock) {
            return backingField
        }
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        setAndGet(value)
    }

    private fun setAndGet(value: T): T {
        synchronized(lock) {
            if (this.backingField != value) {
                this.backingField = value
            }
            return this.backingField
        }
    }
}