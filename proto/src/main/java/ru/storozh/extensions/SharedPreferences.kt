package ru.storozh.extensions

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.storozh.extensions.common.ProtectedLiveData
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * SharedPreferences data simple type
 */
fun <T> SharedPreferences.manage(
    initValue: T, key: (KProperty<*>) -> String = KProperty<*>::name
): ReadWriteProperty<Any, T> = object : ReadWriteProperty<Any, T> {
    private var cache: T? = null

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        Log.d("SharedPreferences", "manage() get property ${key(property)}")
        if (cache.isNull()) {
            if (!checkDataFromSharedPreference(key(property), this@manage)) {
                setDataToSharedPreference(key(property), initValue, this@manage)
            }

            cache = getDataFromSharedPreference(key(property), initValue, this@manage)

        }
        return cache!!
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        Log.d("SharedPreferences", "manage() set property ${key(property)}")
        setDataToSharedPreference(key(property), value, this@manage)
        cache = null
    }
}

/**
 * SharedPreferences data as MutableLiveData
 */
fun <T> SharedPreferences.mutableLiveData(
    initValue: T, key: (KProperty<*>) -> String = KProperty<*>::name
): ReadWriteProperty<Any, MutableLiveData<T>> =
    object : ReadWriteProperty<Any, MutableLiveData<T>> {
        private lateinit var mld: MutableLiveData<T>

        override fun getValue(thisRef: Any, property: KProperty<*>): MutableLiveData<T> {
            initialize(key(property), initValue)
            return mld
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: MutableLiveData<T>) {
            initialize(key(property), value.value ?: initValue)
        }

        private fun initialize(k: String, value: T) {
            if (!this::mld.isInitialized) {
                mld = MutableLiveData(getDataFromSharedPreference(k, value, this@mutableLiveData))
                // Subscribe to changes
                mld.observeForever {
                    setDataToSharedPreference(k, it, this@mutableLiveData)
                }
            }
        }
    }

fun <T> SharedPreferences.mutableLiveDataAsJSON(
    initValue: T, key: (KProperty<*>) -> String = KProperty<*>::name
): ReadWriteProperty<Any, MutableLiveData<T>> =
    object : ReadWriteProperty<Any, MutableLiveData<T>> {
        private lateinit var mld: MutableLiveData<T>

        override fun getValue(thisRef: Any, property: KProperty<*>): MutableLiveData<T> {
            val fromShr: T? =
                getDataFromSharedPreferenceAsJSON(key(property), this@mutableLiveDataAsJSON)
            if (fromShr != null) {
                initialize(key(property), fromShr)
            } else {
                setDataToSharedPreferenceAsJSON(
                    key(property),
                    initValue,
                    this@mutableLiveDataAsJSON
                )
                initialize(key(property), initValue)
            }
            return mld
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: MutableLiveData<T>) {
            val fromShr: T? =
                getDataFromSharedPreferenceAsJSON(key(property), this@mutableLiveDataAsJSON)
            if (fromShr != null) {
                initialize(key(property), fromShr)
            } else {
                setDataToSharedPreferenceAsJSON(
                    key(property),
                    initValue,
                    this@mutableLiveDataAsJSON
                )
                initialize(key(property), initValue)
            }
        }

        private fun initialize(k: String, value: T) {
            if (!this::mld.isInitialized) {
                mld = MutableLiveData(value)
                mld.observeForever {
                    setDataToSharedPreferenceAsJSON(k, it, this@mutableLiveDataAsJSON)
                }
            }
        }

    }


/**
 * SharedPreferences data as ProtectedLiveData
 */
fun <T> SharedPreferences.protectedLiveData(
    initValue: T, key: (KProperty<*>) -> String = KProperty<*>::name, protect: String? = null
): ReadWriteProperty<Any, ProtectedLiveData<T>> =
    object : ReadWriteProperty<Any, ProtectedLiveData<T>> {
        private lateinit var mld: ProtectedLiveData<T>

        override fun getValue(thisRef: Any, property: KProperty<*>): ProtectedLiveData<T> {
            initialize(key(property), initValue)
            return mld
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: ProtectedLiveData<T>) {


            initialize(key(property), value.value ?: initValue)
        }

        private fun initialize(k: String, value: T) {
            if (!this::mld.isInitialized) {
                mld = ProtectedLiveData(
                    getDataFromSharedPreference(k, value, this@protectedLiveData),
                    protect
                )
                // Subscribe to changes
                mld.observeForever {
                    setDataToSharedPreference(k, it, this@protectedLiveData)
                }
            }
        }
    }


private fun <T> setDataToSharedPreferenceAsJSON(
    key: String,
    value: T,
    shp: SharedPreferences
): T {
    shp.edit().apply { putString(key, Gson().toJson(value)) }.apply()
    Log.d(shp.javaClass.name, "Saved value $key = $value to SharedPreferences")
    return value
}

private fun <S> getDataFromSharedPreferenceAsJSON(
    key: String,
    shp: SharedPreferences
): S? {
    val turnsType = object : TypeToken<S>() {}.type
    var value: S? = null
    shp.getString(key, null)?.let {
        value = Gson().fromJson(it, turnsType)
        Log.d(shp.javaClass.name, "Get value $key = $value from SharedPreferences")
    }
    return value
}


private fun <T> setDataToSharedPreference(
    key: String,
    value: T,
    shp: SharedPreferences
): T {
    shp.edit().apply {
        when (value) {
            is Boolean -> putBoolean(key, value as Boolean)
            is String -> putString(key, value as String)
            is Float -> putFloat(key, value as Float)
            is Int -> putInt(key, value as Int)
            is Long -> putLong(key, value as Long)
            else -> throw IllegalArgumentException("This type cannot be saved into Preferences")
        }.apply()
        Log.d(shp.javaClass.name, "Saved value $key = $value to SharedPreferences")
    }
    return value
}

private fun <T> getDataFromSharedPreference(
    key: String,
    initValue: T,
    shp: SharedPreferences
): T {
    return shp.run {
        @Suppress("UNCHECKED_CAST")
        val value = when (initValue) {
            is Int -> getInt(key, initValue) as T
            is Long -> getLong(key, initValue) as T
            is Float -> getFloat(key, initValue) as T
            is String -> getString(key, initValue) as T
            is Boolean -> getBoolean(key, initValue) as T
            else -> throw IllegalArgumentException("This type cannot be saved into Preferences")
        }
        Log.d(shp.javaClass.name, "Get value $key = $value from SharedPreferences")
        value ?: initValue
    }
}

private fun checkDataFromSharedPreference(key: String, shp: SharedPreferences): Boolean =
    shp.contains(key)

