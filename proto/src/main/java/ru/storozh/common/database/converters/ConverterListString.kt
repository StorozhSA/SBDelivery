package ru.storozh.common.database.converters

import androidx.room.TypeConverter
import ru.storozh.common.database.TypeConverterIface

class ConverterListString : TypeConverterIface<List<String>> {

    @TypeConverter
    override fun fromString(value: String): List<String> {
        return value.split(",")
    }

    @TypeConverter
    override fun toString(value: List<String>): String {
        return value.joinToString(",")
    }
}