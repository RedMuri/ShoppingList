package com.example.shoppinglist.domain.shopListUseCases

import com.example.shoppinglist.domain.ShopItem
import com.example.shoppinglist.domain.shopListRepository.ShopListRepository

class GetShopItemUseCase(private val shopListRepository: ShopListRepository) {
    fun getShopItem(shopItemId: Int): ShopItem =
        shopListRepository.getShopItem(shopItemId)
}