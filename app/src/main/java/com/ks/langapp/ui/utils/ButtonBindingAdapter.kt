package com.ks.langapp.ui.utils

import android.view.View
import androidx.databinding.BindingAdapter


@BindingAdapter("setVisibility")
fun setVisibility(view : View, visible : Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.INVISIBLE
}
