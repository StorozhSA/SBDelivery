package ru.storozh.delegates


import ru.storozh.ui.ProtoBinding
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Prop<T : Any>(
    var value: T,
    private val needInit: Boolean = true,
    private val onChange: ((T) -> Unit)? = null
) :
    ReadWriteProperty<ProtoBinding, T> {
    private val listeners: MutableList<() -> Unit> = mutableListOf()


    fun bind() {
        if (needInit) onChange?.invoke(value)
    }

    operator fun provideDelegate(
        thisRef: ProtoBinding,
        prop: KProperty<*>
    ): ReadWriteProperty<ProtoBinding, T> {
        val delegate = Prop(value, needInit, onChange)
        registerDelegate(thisRef, prop.name, delegate)
        return delegate
    }

    override fun getValue(thisRef: ProtoBinding, property: KProperty<*>): T = value

    override fun setValue(thisRef: ProtoBinding, property: KProperty<*>, value: T) {
        if (value == this.value) return
        this.value = value
        onChange?.invoke(this.value)
        if (listeners.isNotEmpty()) listeners.forEach { it.invoke() }
    }

    //register additional listener
    fun addListener(listener: () -> Unit) {
        listeners.add(listener)
    }

    private fun registerDelegate(thisRef: ProtoBinding, name: String, delegate: Prop<T>) {
        thisRef.delegates[name] = delegate
    }
}