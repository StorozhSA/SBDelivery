package ru.storozh.extensions.common

import android.util.Log
import androidx.lifecycle.LiveData

class ProtectedLiveData<T> : LiveData<T> {
    private var protectCode: String? = null

    constructor(value: T, protectCode: String?) : super(value) {
        this.protectCode = protectCode
    }

    constructor(protectCode: String?) : super() {
        this.protectCode = protectCode
    }

    constructor(value: T) : super(value)
    constructor() : super()

    public override fun postValue(value: T) {
        if (protectCode == null) {
            super.postValue(value)
        } else {
            Log.w(
                TAG,
                "The value has not been set. Protection against modification, specify the security code"
            )
        }
    }

    fun postValue(value: T, protectCode: String?) {
        if (this.protectCode == protectCode) {
            super.postValue(value)
        } else {
            Log.w(TAG, "Value not set. Protect codes do not match.")
        }
    }

    public override fun setValue(value: T) {
        if (protectCode == null) {
            super.setValue(value)
        } else {
            Log.w(
                TAG,
                "The value has not been set. Protection against modification, specify the security code"
            )
        }
    }

    fun setValue(value: T, protectCode: String?) {
        if (this.protectCode == protectCode) {
            super.setValue(value)
        } else {
            Log.w(TAG, "Value not set. Protect codes do not match.")
        }
    }

    companion object {
        private val TAG = ProtectedLiveData::class.java.simpleName
    }
}