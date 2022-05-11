package ru.storozh.models.delivery.database.domains

import androidx.paging.DataSource
import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.flow.Flow
import ru.storozh.common.database.DaoIface
import java.io.Serializable

@Entity(tableName = "dishes")
data class Dish(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String?,
    @ColumnInfo(name = "image")
    val image: String,
    @ColumnInfo(name = "old_price")
    val oldPrice: Int?,
    @ColumnInfo(name = "price")
    val price: Int,
    @ColumnInfo(name = "rating")
    val rating: Double,
    @ColumnInfo(name = "comments_count")
    val commentsCount: Int,
    @ColumnInfo(name = "likes")
    val likes: Int,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "active")
    val active: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    @ColumnInfo(name = "favorite")
    val favorite: Boolean
) : Serializable {
    companion object {
        private const val serialVersionUID = 10000000000001L
    }
}

@Dao
interface DishDao : DaoIface<Dish> {

    @Query("SELECT * FROM dishes ORDER BY `name` ASC")
    fun getAll(): List<Dish>

    @Query("SELECT * FROM dishes WHERE id =:id ORDER BY `name` ASC")
    fun getById(id: String): Dish

    @Query("""SELECT DISTINCT * FROM dishes AS d WHERE d.active=1 AND d.favorite=1 ORDER BY `name` ASC""")
    fun getAllFavorites(): List<DishV>

    @Query("SELECT MAX(updated_at) FROM dishes")
    fun getLastModDate(): Long

    @Query("SELECT * FROM dishes WHERE old_price IS NOT NULL")
    fun isPromotionalDish(): Boolean

    @Query("DELETE FROM dishes")
    fun delete()

    @Transaction
    fun upsert(obj: List<Dish>) {
        insert(obj)
            .mapIndexed { index, l -> if (l == -1L) obj[index] else null }
            .filterNotNull()
            .also { if (it.isNotEmpty()) update(it) }
    }
}

@DatabaseView(
    """
select 
d.id, 
d.name, 
d.description, 
d.image, 
d.old_price, 
d.price, 
d.rating, 
d.comments_count, 
d.likes, 
d.category, 
d.active, 
d.created_at, 
d.updated_at, 
d.favorite 
from dishes AS d
union 
select 
d.id, 
d.name, 
d.description, 
d.image, 
d.old_price, 
d.price, 
d.rating, 
d.comments_count, 
d.likes, 
'1' as category, 
d.active, 
d.created_at, 
d.updated_at, 
d.favorite 
from dishes AS d where d.old_price > 0"""
)
data class DishV(
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String?,
    @ColumnInfo(name = "image")
    val image: String,
    @ColumnInfo(name = "old_price")
    val oldPrice: Int?,
    @ColumnInfo(name = "price")
    val price: Int,
    @ColumnInfo(name = "rating")
    val rating: Double,
    @ColumnInfo(name = "comments_count")
    val commentsCount: Int,
    @ColumnInfo(name = "likes")
    val likes: Int,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "active")
    val active: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    @ColumnInfo(name = "favorite")
    val favorite: Boolean
) : Serializable {
    companion object {
        private const val serialVersionUID = 10000000000002L
    }
}

@Dao
interface DishVDao {

    @RawQuery(observedEntities = [DishV::class])
    fun getByParentIdViewDataSource(simpleSQLiteQuery: SimpleSQLiteQuery): DataSource.Factory<Int, DishV>

    @RawQuery(observedEntities = [DishV::class])
    fun getByParentIdViewPagingSource(simpleSQLiteQuery: SimpleSQLiteQuery): PagingSource<Int, DishV>

    @Query("SELECT * FROM DishV WHERE UPPER(name) LIKE UPPER(:text) ORDER BY `name` ASC")
    fun find(text: String): Flow<List<DishV>>

    @Query("""SELECT DISTINCT * FROM DishV AS d LEFT JOIN dishes_recomm AS r ON d.id = r.id WHERE r.id IS NOT NULL AND d.active=1 AND d.category != 1 ORDER BY RANDOM()""")
    fun getAllRecommendation(): Flow<List<DishV>>

    @Query("""SELECT DISTINCT * FROM DishV AS d WHERE d.active=1 AND d.category != 1 AND d.rating >= :minRating ORDER BY RANDOM() LIMIT 10 """)
    fun getAllBest(minRating: Double): Flow<List<DishV>>

    @Query("""SELECT * FROM DishV AS d WHERE d.id IN (SELECT DISTINCT id FROM DishV AS sd WHERE sd.active=1 AND sd.category != 1 AND sd.image IS NOT NULL ORDER BY sd.likes DESC LIMIT 10) ORDER BY RANDOM() LIMIT 10""")
    fun getAllPopular(): Flow<List<DishV>>

    /*    @Query("""SELECT DISTINCT * FROM DishV AS d LEFT JOIN dishes_favorite AS r ON d.id = r.id WHERE r.id IS NOT NULL AND d.active=1 AND d.category != 1 ORDER BY `name` ASC""")
        fun getAllFavorites(): Flow<List<DishV>>*/
    @Query("""SELECT * FROM DishV AS d WHERE d.id=:dishId """)
    fun getById(dishId: String): Flow<DishV>

    @Query("""SELECT DISTINCT * FROM DishV AS d WHERE d.active=1 AND d.category != 1 AND d.favorite=1 ORDER BY `name` ASC""")
    fun getAllFavorites(): Flow<List<DishV>>
}

@Entity(tableName = "dishes_recomm")
data class DishRecommendation(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String
) : Serializable {
    companion object {
        private const val serialVersionUID = 10000000000003L
    }
}

@Dao
interface DishesRecommendationDao : DaoIface<DishRecommendation> {

    @Query("SELECT id FROM dishes_recomm ORDER BY `id` ASC")
    fun getAll(): Flow<List<String>>

    @Query("DELETE FROM dishes_recomm")
    fun delete()

    @Transaction
    fun upsert(obj: List<DishRecommendation>) {
        insert(obj)
            .mapIndexed { index, l -> if (l == -1L) obj[index] else null }
            .filterNotNull()
            .also { if (it.isNotEmpty()) update(it) }
    }
}
