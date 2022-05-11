package ru.storozh.models.delivery.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.storozh.common.database.RoomService
import ru.storozh.common.database.SingletonHolder
import ru.storozh.common.database.converters.ConverterListString
import ru.storozh.models.delivery.database.domains.*

@Database(
    entities = [User::class, Category::class, Dish::class, SearchHistory::class, DishRecommendation::class, Reviews::class, CartItem::class],
    views = [CategoryV::class, DishV::class],
    exportSchema = false,
    version = 1
)
@TypeConverters(ConverterListString::class)
abstract class DeliveryDatabaseService : RoomDatabase(), RoomService {
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun categoryVDao(): CategoryVDao
    abstract fun dishDao(): DishDao
    abstract fun dishVDao(): DishVDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun dishesRecommendationDao(): DishesRecommendationDao
    abstract fun reviewsDao(): ReviewsDao
    abstract fun cartDao(): CartDao

    companion object : SingletonHolder<DeliveryDatabaseService, Context>({
        if (it.getSharedPreferences(it.packageName, Context.MODE_PRIVATE)
                .getBoolean("dbIsRAM", false)
        ) {
            Room.inMemoryDatabaseBuilder(it, DeliveryDatabaseService::class.java).build()
        } else {
            Room.databaseBuilder(it, DeliveryDatabaseService::class.java, it.packageName + ".db")
                .build()
        }
    })
}
