package ru.storozh.common.database


interface TypeConverterIface<T> {
    fun fromString(value: String): T
    fun toString(value: T): String
}