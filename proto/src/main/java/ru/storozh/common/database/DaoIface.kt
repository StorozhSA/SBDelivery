package ru.storozh.common.database

import androidx.room.*


interface DaoIface<T : Any> {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(obj: List<T>): List<Long>

    @Transaction
    @Update
    fun update(obj: List<T>)

    @Transaction
    @Delete
    fun delete(obj: T)

    @Transaction
    @Delete
    fun delete(obj: List<T>): Int
}