package ru.storozh.ui

import java.io.Serializable

interface ProtoViewModelState : Serializable {
    fun isValidState(): Boolean = true

    companion object {
        private const val serialVersionUID = 20000000000001L
    }
}