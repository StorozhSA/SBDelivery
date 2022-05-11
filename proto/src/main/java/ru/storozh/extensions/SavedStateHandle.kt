package ru.storozh.extensions


import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.storozh.extensions.common.ProtectedLiveData
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * SavedStateHandle data as ProtectedLiveData
 */
fun <T> SavedStateHandle.protectedLiveData(
    initValue: T,
    protect: String? = null
): ReadWriteProperty<Any, ProtectedLiveData<T>> =
    object : ReadWriteProperty<Any, ProtectedLiveData<T>>, LifecycleOwner {
        private val mLifecycleRegistry = LifecycleRegistry(this)
        private val dld: ProtectedLiveData<T> = ProtectedLiveData(initValue, protect)
        private lateinit var mld: MutableLiveData<T>

        override fun getValue(thisRef: Any, property: KProperty<*>): ProtectedLiveData<T> {
            initialize(property.name, initValue)
            return dld
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: ProtectedLiveData<T>) {
            initialize(property.name, value.value ?: initValue)
        }

        override fun getLifecycle(): Lifecycle {
            return mLifecycleRegistry
        }

        private fun initialize(key: String, value: T) {
            if (!this::mld.isInitialized) {
                mld = getLiveData(key, value)
                mld.observeForever {
                    if (it != dld.value) {
                        dld.setValue(it, protect)
                    }
                }
                dld.observeForever {
                    if (it != mld.value) {
                        mld.value = it
                    }
                }
            }
        }
    }


/**
 * SavedStateHandle data as MutableLiveData
 */
fun <T> SavedStateHandle.mutableLiveData(initValue: T): ReadWriteProperty<Any, MutableLiveData<T>> =
    object : ReadWriteProperty<Any, MutableLiveData<T>> {

        override operator fun getValue(thisRef: Any, property: KProperty<*>): MutableLiveData<T> {
            return getLiveData(property.name, initValue)
        }

        override operator fun setValue(
            thisRef: Any,
            property: KProperty<*>,
            value: MutableLiveData<T>
        ) {
            getLiveData(property.name, value.value ?: initValue)
        }
    }

/**
 * SavedStateHandle data as MediatorLiveData
 */
fun <T> SavedStateHandle.mediatorLiveData(initValue: T): ReadWriteProperty<Any, MediatorLiveData<T>> =
    object : ReadWriteProperty<Any, MediatorLiveData<T>> {
        private val dld: MediatorLiveData<T> = MediatorLiveData<T>().apply { setValue(initValue) }
        private lateinit var mld: MutableLiveData<T>

        override fun getValue(thisRef: Any, property: KProperty<*>): MediatorLiveData<T> {
            initialize(property.name, initValue)
            return dld
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: MediatorLiveData<T>) {
            initialize(property.name, value.value ?: initValue)
        }

        private fun initialize(key: String, value: T) {
            if (!this::mld.isInitialized) {
                mld = getLiveData(key, value)
                mld.observeForever {
                    if (it != dld.value) {
                        dld.value = it
                    }
                }
                dld.observeForever {
                    if (it != mld.value) {
                        mld.value = it
                    }
                }
            }
        }
    }

/**
 * SavedStateHandle data as State
 */
fun <T> SavedStateHandle.justState(initValue: T) = object : ReadWriteProperty<Any, T> {

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        logd("SavedStateHandle.justState get value by name ${property.name}")
        return this@justState.get<T>(property.name) ?: let {
            setValue(thisRef, property, initValue)
            initValue
        }
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        logd("SavedStateHandle.justState set value = $value")
        this@justState.set(property.name, value)
    }
}


/**
 * SavedStateHandle data as mutableStateFlow
 */
fun <T> SavedStateHandle.mutableStateFlow(
    initValue: T,
    scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext)
) = object : ReadOnlyProperty<Any, MutableStateFlow<T>> {
    private val msf = MutableStateFlow(initValue)
    private lateinit var mld: MutableLiveData<T>

    override fun getValue(thisRef: Any, property: KProperty<*>): MutableStateFlow<T> {
        logd("SavedStateHandle.mutableStateFlow get value by name ${property.name}")
        initialize(property.name, initValue)
        return msf
    }

    private fun initialize(key: String, value: T) {
        if (!this::mld.isInitialized) {
            mld = getLiveData(key, value)
            mld.observeForever {
                if (it != msf.value) {
                    scope.launch { msf.emit(it) }
                }
            }
            scope.launch {
                msf.collect {
                    if (it != mld.value) {
                        mld.value = it
                    }
                }
            }
        }
    }
}
