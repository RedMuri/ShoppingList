package com.example.shoppinglist.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shoppinglist.domain.ShopItem

@Dao
interface ShopListDao {

    @Query("select * from shop_items")
    fun getShopList(): LiveData<List<ShopItemDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addShopItem(shopItemDbModel: ShopItemDbModel)

    @Query("delete from shop_items where id=:shopItemId")
    fun deleteShopItem(shopItemId: Int)

    @Query("select * from shop_items where id=:shopItemId limit 1")
    fun getShopItem(shopItemId: Int): ShopItemDbModel
}