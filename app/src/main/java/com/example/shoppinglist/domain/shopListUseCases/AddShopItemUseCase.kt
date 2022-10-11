package com.example.shoppinglist.domain.shopListUseCases

import com.example.shoppinglist.domain.ShopItem
import com.example.shoppinglist.domain.shopListRepository.ShopListRepository

class AddShopItemUseCase(private val shopListRepository: ShopListRepository) {
    suspend fun addShopItem(shopItem: ShopItem) =
        shopListRepository.addShopItem(shopItem)
}