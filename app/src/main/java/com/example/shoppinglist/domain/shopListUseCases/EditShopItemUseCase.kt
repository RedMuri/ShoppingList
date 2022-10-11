package com.example.shoppinglist.domain.shopListUseCases

import com.example.shoppinglist.domain.ShopItem
import com.example.shoppinglist.domain.shopListRepository.ShopListRepository

class EditShopItemUseCase(private val shopListRepository: ShopListRepository) {
    suspend fun editShopItem(shopItem: ShopItem) =
        shopListRepository.editShopItem(shopItem)
}