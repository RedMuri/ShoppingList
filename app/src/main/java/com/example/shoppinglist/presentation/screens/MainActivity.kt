package com.example.shoppinglist.presentation.screens

import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract.Contacts
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.databinding.ActivityMainBinding
import com.example.shoppinglist.presentation.Contact
import com.example.shoppinglist.presentation.ShopListApp
import com.example.shoppinglist.presentation.adapters.ShopListAdapter
import com.example.shoppinglist.presentation.view_models.MainViewModel
import com.example.shoppinglist.presentation.view_models.ViewModelFactory
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

        val permissionGranted = ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        if (permissionGranted)
            requestContacts()
        else
            requestPermission()
    }

    private fun requestContacts() {
        thread {
            val cursor = contentResolver.query(
                Contacts.CONTENT_URI,
                null,
                null,
                null,
                null
            )
            while (cursor?.moveToNext() == true) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(Contacts._ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(Contacts.DISPLAY_NAME))
                val contact = Contact(id, name)
                Log.d("muri", contact.toString())
            }
            cursor?.close()
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_CONTACTS),
            READ_CONTACTS_RC
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == READ_CONTACTS_RC && grantResults.isNotEmpty()){
            val permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED

            if (permissionGranted)
                requestContacts()
            else
                Log.d("muri","denied final")
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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

            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onEditingFinish() {
        supportFragmentManager.popBackStack()
    }

    companion object {
        private const val READ_CONTACTS_RC = 100
    }
}