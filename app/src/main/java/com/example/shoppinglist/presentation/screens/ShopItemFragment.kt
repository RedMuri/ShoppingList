package com.example.shoppinglist.presentation.screens

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinglist.R
import com.example.shoppinglist.databinding.FragmentShopItemBinding
import com.example.shoppinglist.domain.ShopItem
import com.example.shoppinglist.presentation.ShopListApp
import com.example.shoppinglist.presentation.view_models.ShopItemViewModel
import com.example.shoppinglist.presentation.view_models.ViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import javax.inject.Inject
import kotlin.concurrent.thread


class ShopItemFragment : Fragment() {
    private var _binding: FragmentShopItemBinding? = null
    private val binding: FragmentShopItemBinding
        get() = _binding ?: throw RuntimeException("FragmentShopItemBinding = null!")

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val shopItemViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[ShopItemViewModel::class.java]
    }

    private val component by lazy {
        (requireActivity().application as ShopListApp).component
    }

    private var screenMode: String = MODE_UNKNOWN
    private var shopItemId: Int = ShopItem.UNDEFINED_ID

    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
        onEditingFinishedListener = if (context is OnEditingFinishedListener) {
            context
        } else {
            throw RuntimeException("Activity should implement OnEditingFinishedListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentShopItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = shopItemViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        launchRightMode()
        addTextChangeListeners()
        observeViewModel()
    }


    private fun launchRightMode() {
        when (screenMode) {
            MODE_ADD -> launchAddMode()
            MODE_EDIT -> launchEditMode()
        }
    }

    private fun observeViewModel() {
        shopItemViewModel.shouldCloseScreen.observe(viewLifecycleOwner) {
            onEditingFinishedListener.onEditingFinish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addTextChangeListeners() {
        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                shopItemViewModel.resetErrorInputName()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.etCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                shopItemViewModel.resetErrorInputCount()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun launchAddMode() {
        binding.saveButton.setOnClickListener {
            //shopItemViewModel.addShopItem(binding.etName.text?.toString(), binding.etCount.text?.toString())
            thread {
                context?.contentResolver?.insert(
                    Uri.parse("content://com.example.shoppinglist/shop_items"),
                    ContentValues().apply {
                        put("id", 0)
                        put("count", binding.etCount.text?.toString()?.toInt())
                        put("name", binding.etName.text?.toString())
                        put("enabled", true)
                    }
                )

            }
        }
    }

    private fun launchEditMode() {
        shopItemViewModel.getShopItem(shopItemId)
        binding.saveButton.setOnClickListener {
            shopItemViewModel.editShopItem(binding.etName.text?.toString(),
                binding.etCount.text?.toString())
        }
    }


    private fun parseParams() {
        val args = requireArguments()
        if (!args.containsKey(SCREEN_MODE)) {
            throw RuntimeException("Param screen mode is absent!")
        }
        val mode = args.getString(SCREEN_MODE)
        if (mode != MODE_ADD && mode != MODE_EDIT) {
            throw RuntimeException("Unknown screen mode!")
        }
        screenMode = mode
        if (screenMode == MODE_EDIT) {
            if (!args.containsKey(SHOP_ITEM_ID)) {
                throw RuntimeException("Param shop item id is absent!")
            }
            shopItemId = args.getInt(SHOP_ITEM_ID, ShopItem.UNDEFINED_ID)
        }
    }


    interface OnEditingFinishedListener {
        fun onEditingFinish()
    }

    companion object {
        private const val SHOP_ITEM_ID = "shop_item_id"
        private const val MODE_UNKNOWN = ""

        private const val SCREEN_MODE = "extra_screen_mode"
        private const val MODE_ADD = "mode_add"
        private const val MODE_EDIT = "mode_edit"

        fun newInstanceAddItem(): Fragment {
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_ADD)
                }
            }
        }

        fun newInstanceEditItem(shopItemId: Int): Fragment {
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_EDIT)
                    putInt(SHOP_ITEM_ID, shopItemId)
                }
            }
        }
    }
}
