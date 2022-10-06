package com.example.shoppinglist.presentation.view_models

import androidx.lifecycle.ViewModel
import com.example.shoppinglist.data.shopListRepositoryImpl.ShopListRepositoryImpl
import com.example.shoppinglist.domain.ShopItem
import com.example.shoppinglist.domain.shopListUseCases.AddShopItemUseCase
import com.example.shoppinglist.domain.shopListUseCases.EditShopItemUseCase
import com.example.shoppinglist.domain.shopListUseCases.GetShopItemUseCase
import java.util.concurrent.CountDownLatch

class ShopItemViewModel : ViewModel() {
    private val repository = ShopListRepositoryImpl
    private val addShopItemUseCase = AddShopItemUseCase(repository)
    private val editShopItemUseCase = EditShopItemUseCase(repository)
    private val getShopItemUseCase = GetShopItemUseCase(repository)

    fun addShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldsValid = validateInput(name, count)
        if (fieldsValid) {
            val shopItem = ShopItem(name, count, true)
            addShopItemUseCase.addShopItem(shopItem)
        }
    }

    fun editShopItem(editName: String?, editCount: String?) {
        val name = parseName(editName)
        val count = parseCount(editCount)
        val fieldsValid = validateInput(name, count)
        if (fieldsValid) {
            val shopItem = ShopItem(name, count, true)
            editShopItemUseCase.editShopItem(shopItem)
        }
    }

    fun getShopItem(shopItemId: Int): ShopItem {
        return getShopItemUseCase.getShopItem(shopItemId)
    }

    private fun parseName(inputName: String?): String {
        return inputName?.trim() ?: ""
    }

    private fun parseCount(inputCount: String?): Int {
        return try {
            inputCount?.trim()?.toInt() ?: 0
        } catch (e: java.lang.NumberFormatException) {
            0
        }
    }

    private fun validateInput(name: String, count: Int): Boolean {
        var result = true
        if (name.isBlank()) {
            // TODO: show error input name
            result = false
        }
        if (count <= 0) {
            // TODO: show error input count
            result = false
        }
        return result
    }
}