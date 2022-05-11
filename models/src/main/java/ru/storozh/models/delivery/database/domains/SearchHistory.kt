package ru.storozh.models.delivery.database.domains

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.storozh.common.database.DaoIface
import java.util.*

@Entity(tableName = "history")
data class SearchHistory(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = "",
    @ColumnInfo(name = "created_at", defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Long = Date().time
)

@Dao
interface SearchHistoryDao : DaoIface<SearchHistory> {

    @Query("SELECT COUNT(id) FROM history")
    fun recordsCount(): Int

    @Query("SELECT id FROM history ORDER BY created_at DESC LIMIT 5")
    fun getHistory(): Flow<List<String>>

    @Transaction
    @Query("DELETE FROM history WHERE id NOT IN (SELECT id FROM history ORDER BY created_at DESC LIMIT 5)")
    fun cleanOldItems()

    @Transaction
    @Query("DELETE FROM history WHERE id =:id")
    fun delete(id: String)

    @Transaction
    fun upsert(obj: List<SearchHistory>) {
        insert(obj)
            .mapIndexed { index, l -> if (l == -1L) obj[index] else null }
            .filterNotNull()
            .also { if (it.isNotEmpty()) update(it) }
    }
}
