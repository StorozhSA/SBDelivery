package ru.storozh.common

sealed class Notify {
    abstract val message: String

    data class TextMessage(override val message: String) : Notify()
    data class LinkMessage(override val message: String) : Notify()
    data class ActionMessage(
        override val message: String,
        val actionLabel: String,
        val actionHandler: (() -> Unit)
    ) : Notify()

    data class ErrorMessage(
        override val message: String,
        val errLabel: String?,
        val errHandler: (() -> Unit)?
    ) : Notify()
}