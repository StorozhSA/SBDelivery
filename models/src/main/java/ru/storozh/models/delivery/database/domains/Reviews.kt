package ru.storozh.models.delivery.database.domains

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.storozh.common.database.DaoIface
import java.io.Serializable

@Entity(tableName = "reviews")
data class Reviews(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "dishId")
    val dishId: String,
    @ColumnInfo(name = "active")
    val active: Boolean,
    @ColumnInfo(name = "author")
    val author: String,
    @ColumnInfo(name = "createdAt")
    val createdAt: Long,
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "rating")
    val rating: Int,
    @ColumnInfo(name = "text")
    val text: String,
    @ColumnInfo(name = "updatedAt")
    val updatedAt: Long
) : Serializable {
    companion object {
        private const val serialVersionUID = 10340000000001L
    }
}

@Dao
interface ReviewsDao : DaoIface<Reviews> {

    @Query("SELECT * FROM reviews WHERE dishId =:dishId ORDER BY `date` DESC")
    fun getByDishId(dishId: String): Flow<List<Reviews>>

    @Query("DELETE FROM reviews")
    fun delete()

    @Transaction
    fun upsert(obj: List<Reviews>) {
        insert(obj)
            .mapIndexed { index, l -> if (l == -1L) obj[index] else null }
            .filterNotNull()
            .also { if (it.isNotEmpty()) update(it) }
    }
}
