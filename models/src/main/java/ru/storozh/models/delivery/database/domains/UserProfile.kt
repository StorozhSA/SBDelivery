package ru.storozh.models.delivery.database.domains

import androidx.room.*
import ru.storozh.common.database.DaoIface

@Entity(tableName = "register")
data class User(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = "",
    @ColumnInfo(name = "access_token")
    val accessToken: String = "",
    @ColumnInfo(name = "refresh_token")
    val refreshToken: String = "",
    @ColumnInfo(name = "email")
    val email: String = "",
    @ColumnInfo(name = "first_name")
    val firstName: String = "",
    @ColumnInfo(name = "last_name")
    val lastName: String = ""
)

@Dao
interface UserDao : DaoIface<User> {

    @Query("SELECT COUNT(id) FROM register")
    fun recordsCount(): Int

    @Query("SELECT * FROM register")
    fun get(): List<User>

    @Query("SELECT * FROM register WHERE id =:id")
    fun get(id: String): List<User>

    @Transaction
    @Query("DELETE FROM register")
    fun delete()

    @Transaction
    fun upsert(obj: List<User>) {
        insert(obj)
            .mapIndexed { index, l -> if (l == -1L) obj[index] else null }
            .filterNotNull()
            .also { if (it.isNotEmpty()) update(it) }
    }
}
