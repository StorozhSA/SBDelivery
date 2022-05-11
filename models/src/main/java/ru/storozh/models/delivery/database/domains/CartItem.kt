package ru.storozh.models.delivery.database.domains

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.storozh.common.database.DaoIface
import java.io.Serializable

@Entity(tableName = "cart")
data class CartItem(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "amount")
    val amount: Int = 0,
    @ColumnInfo(name = "price")
    val price: Int = 0
) : Serializable {
    companion object {
        private const val serialVersionUID = 10000300000004L
    }
}

data class CartItemJoined(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "amount")
    val amount: Int = 0,
    @ColumnInfo(name = "price")
    val price: Int = 0,
    @ColumnInfo(name = "name")
    val name: String = "",
    @ColumnInfo(name = "image")
    val image: String = ""
) : Serializable {
    fun asCartItem(): CartItem = CartItem(id = id, amount = amount, price = price)

    companion object {
        private const val serialVersionUID = 10000400000004L
    }
}

@Dao
interface CartDao : DaoIface<CartItem> {
    @Query("SELECT  count(c.id) FROM cart as c")
    fun count(): Int

    @Query("SELECT  c.id, c.amount, c.price, d.name, d.image FROM cart AS c LEFT JOIN dishes AS d ON c.id=d.id ORDER BY d.name ASC")
    fun getCart(): Flow<List<CartItemJoined>>

    @Query("SELECT  c.id, c.amount, c.price, d.name, d.image FROM cart AS c LEFT JOIN dishes AS d ON c.id=d.id WHERE c.id = :dishId ORDER BY d.name ASC")
    fun getCart(dishId: String): CartItemJoined?

    @Query("DELETE FROM cart")
    fun delete(): Int

    @Query("DELETE FROM cart WHERE id=:id")
    fun delete(id: String): Int

    @Transaction
    fun upsert(obj: List<CartItem>) {
        insert(obj)
            .mapIndexed { index, l -> if (l == -1L) obj[index] else null }
            .filterNotNull()
            .also { if (it.isNotEmpty()) update(it) }
    }
}
