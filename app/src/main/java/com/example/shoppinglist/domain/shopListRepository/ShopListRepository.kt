package com.example.shoppinglist.domain.shopListRepository

import androidx.lifecycle.LiveData
import com.example.shoppinglist.domain.ShopItem

interface ShopListRepository {
    fun addShopItem(shopItem: ShopItem)

    fun deleteShopItem(shopItem: ShopItem)

    fun editShopItem(shopItem: ShopItem)

    fun getShopItem(shopItemId: Int): ShopItem

    fun getShopList(): LiveData<List<ShopItem>>
}