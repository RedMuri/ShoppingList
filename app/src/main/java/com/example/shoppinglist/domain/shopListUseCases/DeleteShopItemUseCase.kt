package com.example.shoppinglist.domain.shopListUseCases

import com.example.shoppinglist.domain.ShopItem
import com.example.shoppinglist.domain.shopListRepository.ShopListRepository

class DeleteShopItemUseCase(private val shopListRepository: ShopListRepository) {
    fun deleteShopItem(shopItem: ShopItem) =
        shopListRepository.deleteShopItem(shopItem)
}