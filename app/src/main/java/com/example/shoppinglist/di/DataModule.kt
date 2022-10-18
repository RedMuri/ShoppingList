package com.example.shoppinglist.di

import android.app.Application
import com.example.shoppinglist.data.database.AppDatabase
import com.example.shoppinglist.data.database.ShopListDao
import com.example.shoppinglist.data.shop_list_repository_impl.ShopListRepositoryImpl
import com.example.shoppinglist.domain.shopListRepository.ShopListRepository
import dagger.Binds
import dagger.Module
import dagger.Provides


@Module
interface DataModule {

    @ApplicationScope
    @Binds
    fun bindShopListRepository(impl: ShopListRepositoryImpl): ShopListRepository

    companion object{

        @ApplicationScope
        @Provides
        fun provideShopListDao(application: Application): ShopListDao {
            return AppDatabase.getInstance(application).shopListDao()
        }
    }
}