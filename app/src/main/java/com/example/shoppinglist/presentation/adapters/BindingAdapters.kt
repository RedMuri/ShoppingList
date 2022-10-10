package com.example.shoppinglist.presentation.adapters

import androidx.databinding.BindingAdapter
import com.example.shoppinglist.R
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("errorInputName")
fun bindErrorInputName(textInputLayout: TextInputLayout, isError: Boolean){
    if (isError) {
        textInputLayout.error = textInputLayout.context.getString(R.string.til_name_error)
    } else {
        textInputLayout.error = null
    }
}

@BindingAdapter("errorInputCount")
fun bindErrorInputCount(textInputLayout: TextInputLayout, isError: Boolean){
    if (isError) {
        textInputLayout.error = textInputLayout.context.getString(R.string.til_count_error)
    } else {
        textInputLayout.error = null
    }
}