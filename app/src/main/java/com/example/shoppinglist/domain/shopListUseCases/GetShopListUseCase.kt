package com.example.shoppinglist.domain.shopListUseCases

import androidx.lifecycle.LiveData
import com.example.shoppinglist.domain.ShopItem
import com.example.shoppinglist.domain.shopListRepository.ShopListRepository
import javax.inject.Inject

class GetShopListUseCase @Inject constructor(private val shopListRepository: ShopListRepository) {
    fun getShopList(): LiveData<List<ShopItem>> =
        shopListRepository.getShopList()
}