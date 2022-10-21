package com.example.shoppinglist.presentation.screens

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.databinding.ActivityMainBinding
import com.example.shoppinglist.domain.ShopItem
import com.example.shoppinglist.presentation.ShopListApp
import com.example.shoppinglist.presentation.view_models.MainViewModel
import com.example.shoppinglist.presentation.adapters.ShopListAdapter
import com.example.shoppinglist.presentation.view_models.ViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import javax.inject.Inject
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), ShopItemFragment.OnEditingFinishedListener {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    private val component by lazy {
        (application as ShopListApp).component
    }

    private lateinit var shopListAdapter: ShopListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()
        viewModel.shopList.observe(this) {
            shopListAdapter.submitList(it)
        }
        binding.buttonAddShopItem.setOnClickListener { buttonAddShopItem() }
        thread {
            val cursor = contentResolver.query(
                Uri.parse("content://com.example.shoppinglist/shop_items"),
                null,
                null,
                null,
                null,
                null
            )
            while (cursor?.moveToNext() == true) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val count = cursor.getInt(cursor.getColumnIndexOrThrow("count"))
                val enabled = cursor.getInt(cursor.getColumnIndexOrThrow("enabled")) > 0
                val shopItem = ShopItem(id = id, name = name, count = count, enabled = enabled)
                Log.i("muri", "shopItem: $shopItem")
            }
            cursor?.close()
        }
    }

    private fun buttonAddShopItem() {
        if (isOnePaneMode()) {
            val intent = ShopItemActivity.newIntentAddItem(this)
            startActivity(intent)
        } else {
            val fragment = ShopItemFragment.newInstanceAddItem()
            launchFragment(fragment)
        }
    }

    private fun setupRecyclerView() {
        with(binding.rvShopList) {
            shopListAdapter = ShopListAdapter()
            adapter = shopListAdapter
            recycledViewPool.setMaxRecycledViews(ShopListAdapter.VIEW_TYPE_ENABLED,
                ShopListAdapter.MAX_POOL_SIZE)
            recycledViewPool.setMaxRecycledViews(ShopListAdapter.VIEW_TYPE_DISABLED,
                ShopListAdapter.MAX_POOL_SIZE)
        }
        this.setupOnLongClickListener()
        this.setupOnClickListener()
        setupSwipeListener(binding.rvShopList)
    }

    private fun setupOnLongClickListener() {
        shopListAdapter.onShopItemLongClickListener = {
            viewModel.editShopItem(it)
        }
    }

    private fun setupOnClickListener() {
        shopListAdapter.onShopItemClickListener = {
            if (isOnePaneMode()) {
                val intent = ShopItemActivity.newIntentEditItem(this, it.id)
                startActivity(intent)
            } else {
                val fragment = ShopItemFragment.newInstanceEditItem(it.id)
                launchFragment(fragment)
            }
        }
    }

    private fun isOnePaneMode() = binding.shopItemContainer == null

    private fun launchFragment(fragment: Fragment) {
        supportFragmentManager.popBackStack()
        supportFragmentManager.beginTransaction()
            .replace(R.id.shop_item_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setupSwipeListener(recyclerView: RecyclerView) {
        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = shopListAdapter.currentList[viewHolder.adapterPosition]
                viewModel.deleteShopItem(item)
                thread{
                    contentResolver.delete(
                        Uri.parse("content://com.example.shoppinglist/shop_items"),
                        null,
                        arrayOf(item.id.toString())
                    )
                }
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onEditingFinish() {
        supportFragmentManager.popBackStack()
    }
}