package ru.skillbranch.sbdelivery

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import ru.skillbranch.sbdelivery.App.Companion.mappingFavorites
import ru.storozh.models.delivery.network.domains.ResFavoriteItem

class ListComprasionTest {

    @Before
    fun method() {
    }

    @Test
    fun test_comprasion_list() {
        val originalListFromServer = mutableListOf<ResFavoriteItem>()
        val originalListFromLocal = mutableListOf<ResFavoriteItem>()
        val controlSet = setOf(
            ResFavoriteItem(dishId = "1", favorite = true, updatedAt = 300),
            ResFavoriteItem(dishId = "2", favorite = true, updatedAt = 200),
            ResFavoriteItem(dishId = "3", favorite = true, updatedAt = 200),
            ResFavoriteItem(dishId = "4", favorite = true, updatedAt = 200)
        )

        originalListFromServer.add(ResFavoriteItem(dishId = "1", favorite = true, updatedAt = 200))
        originalListFromServer.add(ResFavoriteItem(dishId = "2", favorite = true, updatedAt = 200))
        // originalListFromServer.add(ResFavoriteItem(dishId = "3", favorite = true, updatedAt = 200))
        originalListFromServer.add(ResFavoriteItem(dishId = "4", favorite = true, updatedAt = 200))

        originalListFromLocal.add(ResFavoriteItem(dishId = "1", favorite = true, updatedAt = 300))
        originalListFromLocal.add(ResFavoriteItem(dishId = "2", favorite = true, updatedAt = 100))
        originalListFromLocal.add(ResFavoriteItem(dishId = "3", favorite = true, updatedAt = 200))
        // originalListFromLocal.add(ResFavoriteItem(dishId = "4", favorite = true, updatedAt = 200))

        Assert.assertEquals(
            controlSet,
            mappingFavorites(originalListFromLocal, originalListFromServer)
        )
    }

    @Test
    fun test_eq_favorites_item() {
        val a = ResFavoriteItem(dishId = "1", favorite = true, updatedAt = 300)
        val b = ResFavoriteItem(dishId = "1", favorite = true, updatedAt = 300)
        Assert.assertEquals(a, b)

        val c = ResFavoriteItem(dishId = "1", favorite = true, updatedAt = 300)
        val d = ResFavoriteItem(dishId = "1", favorite = true, updatedAt = 200)
        Assert.assertNotEquals(c, d)
    }

    @Test
    fun test_eq_favorites_item_set() {
        val a = ResFavoriteItem(dishId = "1", favorite = true, updatedAt = 300)
        val b = ResFavoriteItem(dishId = "1", favorite = true, updatedAt = 300)
        val c = ResFavoriteItem(dishId = "1", favorite = true, updatedAt = 300)
        val d = ResFavoriteItem(dishId = "1", favorite = true, updatedAt = 200)

        val favorites = mutableSetOf<ResFavoriteItem>()
        favorites.add(a)
        favorites.add(b)
        favorites.add(c)
        favorites.add(d)

        Assert.assertEquals(2, favorites.size)
    }
}
