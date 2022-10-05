package com.example.shoppinglist.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinglist.R
import com.example.shoppinglist.domain.ShopItem

class MainActivity : AppCompatActivity() {
    private lateinit var linearLayout: LinearLayout
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        linearLayout = findViewById(R.id.linearLayout)
        viewModel.shopList.observe(this) {
            showList(it)
        }
    }

    private fun showList(list: List<ShopItem>) {
        linearLayout.removeAllViews()
        for (shopItem in list) {
            val layoutId = if (shopItem.enabled)
                R.layout.item_shop_enabled
            else
                R.layout.item_shop_disabled
            val view = LayoutInflater.from(this).inflate(layoutId,linearLayout,false)
            val tvCount = view.findViewById<TextView>(R.id.tv_count)
            val tvName = view.findViewById<TextView>(R.id.tv_name)
            tvCount.text = shopItem.count.toString()
            tvName.text = shopItem.name
            view.setOnLongClickListener{
                viewModel.editShopItem(shopItem)
                true
            }
            linearLayout.addView(view)
        }
    }
}