package com.ks.langapp.ui.adapters

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputEditText
import com.ks.langapp.R
import com.ks.langapp.data.database.entities.CardStats
import com.ks.langapp.data.database.entities.Deck
import com.ks.langapp.ui.importcards.ImportViewModel
import com.ks.langapp.ui.importcards.ImportViewModel.*
import java.util.*

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

@BindingAdapter("deckButtonText")
fun deckButtonText(view : View, deck: Deck?) {
    (view as Button).text = deck?.name ?: view.resources.getText(R.string.choose_deck)
}

@BindingAdapter("textMapLanguage")
fun textMapLanguage(view : View, language: String?) {
    language?.let {
        (view as TextInputEditText).setText(Locale(language).displayLanguage)
    }
}

@BindingAdapter("dividedStatsResult")
fun dividedStatsResult(view : View, cardStats: CardStats?) {
    val divided = if (cardStats != null) cardStats.easeScore.toFloat() / cardStats.timesReviewed else 0
    (view as TextView).text = divided.toString()
}
