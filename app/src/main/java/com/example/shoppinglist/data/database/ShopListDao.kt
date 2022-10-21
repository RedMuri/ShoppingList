package com.example.shoppinglist.data.database

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ShopListDao {

    @Query("select * from shop_items")
    fun getShopList(): LiveData<List<ShopItemDbModel>>

    @Query("select * from shop_items")
    fun getShopListCursor(): Cursor

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addShopItemSync(shopItemDbModel: ShopItemDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addShopItem(shopItemDbModel: ShopItemDbModel)

    @Query("delete from shop_items where id=:shopItemId")
    suspend fun deleteShopItem(shopItemId: Int)

    @Query("delete from shop_items where id=:shopItemId")
    fun deleteShopItemSync(shopItemId: Int): Int

    @Query("select * from shop_items where id=:shopItemId limit 1")
    suspend fun getShopItem(shopItemId: Int): ShopItemDbModel
}