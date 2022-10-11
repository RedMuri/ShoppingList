package com.example.shoppinglist.presentation.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppinglist.data.shop_list_repository_impl.ShopListRepositoryImpl
import com.example.shoppinglist.domain.ShopItem
import com.example.shoppinglist.domain.shopListUseCases.DeleteShopItemUseCase
import com.example.shoppinglist.domain.shopListUseCases.EditShopItemUseCase
import com.example.shoppinglist.domain.shopListUseCases.GetShopListUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val shopListRepository = ShopListRepositoryImpl(application)

    private val getShopListUseCase = GetShopListUseCase(shopListRepository)
    private val deleteShopItemUseCase = DeleteShopItemUseCase(shopListRepository)
    private val editShopItemUseCase = EditShopItemUseCase(shopListRepository)

    val shopList = getShopListUseCase.getShopList()

    fun deleteShopItem(shopItem: ShopItem) {
        viewModelScope.launch {
            deleteShopItemUseCase.deleteShopItem(shopItem)
        }
    }

    fun editShopItem(shopItem: ShopItem) {
        viewModelScope.launch {
            val newItem = shopItem.copy(enabled = !shopItem.enabled)
            editShopItemUseCase.editShopItem(newItem)
        }
    }
}