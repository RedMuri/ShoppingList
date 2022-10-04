package com.example.shoppinglist.domain

import androidx.lifecycle.LiveData

class GetShopListUseCase(private val shopListRepository: ShopListRepository) {
    fun getShopList(): List<ShopItem> =
        shopListRepository.getShopList()
}