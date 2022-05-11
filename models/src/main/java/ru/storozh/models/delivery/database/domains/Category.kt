package ru.storozh.models.delivery.database.domains

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.storozh.common.database.DaoIface
import java.io.Serializable

@Entity(tableName = "category")
data class Category(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "order")
    val order: Int,
    @ColumnInfo(name = "icon")
    val icon: String,
    @ColumnInfo(name = "parent")
    val parent: String,
    @ColumnInfo(name = "active")
    val active: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
) : Comparable<Category>, Serializable {
    override fun compareTo(other: Category): Int = order - other.order

    companion object {
        private const val serialVersionUID = 10000000000004L
    }
}

@Dao
interface CategoryDao : DaoIface<Category> {

    @Query("SELECT * FROM category WHERE active=1 AND parent=:parentId ORDER BY `order` ASC")
    fun getByParentIdFlow(parentId: String): Flow<List<Category>>

    @Query("SELECT MAX(updated_at) FROM category")
    fun getLastModDate(): Long

    @Query("DELETE FROM category")
    fun delete()

    @Transaction
    fun upsert(obj: List<Category>) {
        insert(obj)
            .mapIndexed { index, l -> if (l == -1L) obj[index] else null }
            .filterNotNull()
            .also { if (it.isNotEmpty()) update(it) }
    }
}

@DatabaseView(
    """
select  cat.*, count(dishes.id) as is_dish_root from category as cat left join dishes on cat.id=dishes.category group by cat.id 
union 
select '1' as id, 'Акции' as name, 0 as 'order', '' as icon, 'root' as parent, count(dishes.id) > 0  as 'active', 0 as 'created_at', 0 as 'updated_at', count(dishes.id) as is_dish_root from dishes where old_price > 0
"""
)
data class CategoryV(
    // @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "order")
    val order: Int,
    @ColumnInfo(name = "icon")
    val icon: String,
    @ColumnInfo(name = "parent")
    val parent: String,
    @ColumnInfo(name = "active")
    val active: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    @ColumnInfo(name = "is_dish_root")
    val isDishRoot: Boolean
) : Comparable<CategoryV>, Serializable {
    override fun compareTo(other: CategoryV): Int = order - other.order

    companion object {
        private const val serialVersionUID = 10000000000005L
    }
}

@Dao
interface CategoryVDao {
    @Query("SELECT * FROM CategoryV WHERE active=1 AND parent=:parentId ORDER BY `order` ASC")
    fun getByParentIdFlow(parentId: String): Flow<List<CategoryV>>

    @Query("SELECT * FROM CategoryV WHERE UPPER(name) LIKE UPPER(:text) ORDER BY `name` ASC")
    fun find(text: String): Flow<List<CategoryV>>
}
