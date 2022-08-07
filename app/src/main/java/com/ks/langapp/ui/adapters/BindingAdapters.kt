package com.ks.langapp.ui.adapters

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.ks.langapp.ui.importcards.ImportViewModel
import com.ks.langapp.ui.importcards.ImportViewModel.*

@BindingAdapter("setVisibility")
fun setVisibility(view : View, visible : Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("separatorText")
fun separatorText(view : View, separator: Separator) {
    (view as TextView).text = when (separator) {
        is Separator.CharSeparator -> {
            separator.char.toString()
        }
        is Separator.NewLine -> {
            "<NEW LINE>"
        }
    }
}

@BindingAdapter("otherTermText")
fun otherTermText(view : View, term : Term) {
    (view as TextView).text = when (term) {
        Term.FRONT -> "BACK"
        Term.BACK -> "FRONT"
    }
}
