package com.example.shoppinglist.presentation.view_models

import androidx.lifecycle.ViewModel
import com.example.shoppinglist.data.shop_list_repository_impl.ShopListRepositoryImpl
import com.example.shoppinglist.domain.shopListUseCases.DeleteShopItemUseCase
import com.example.shoppinglist.domain.shopListUseCases.EditShopItemUseCase
import com.example.shoppinglist.domain.shopListUseCases.GetShopListUseCase
import com.example.shoppinglist.domain.ShopItem

class MainViewModel: ViewModel() {
    private val shopListRepository = ShopListRepositoryImpl

    private val getShopListUseCase = GetShopListUseCase(shopListRepository)
    private val deleteShopItemUseCase = DeleteShopItemUseCase(shopListRepository)
    private val editShopItemUseCase = EditShopItemUseCase(shopListRepository)

    val shopList = getShopListUseCase.getShopList()


    fun deleteShopItem(shopItem: ShopItem){
        deleteShopItemUseCase.deleteShopItem(shopItem)
    }

    fun editShopItem(shopItem: ShopItem){
        val newItem = shopItem.copy(enabled = !shopItem.enabled)
        editShopItemUseCase.editShopItem(newItem)
    }
}