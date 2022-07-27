package com.ks.langapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ks.langapp.data.database.entities.Deck
import com.ks.langapp.databinding.ItemDeckBinding

class DecksAdapter(private val clickListener: DecksListener) :
    ListAdapter<Deck, DecksAdapter.ViewHolder>(DecksDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: ItemDeckBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(deck: Deck, clickListener: DecksListener) {
            binding.deck = deck
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDeckBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class DecksDiffCallback : DiffUtil.ItemCallback<Deck>() {
    override fun areItemsTheSame(oldItem: Deck, newItem: Deck): Boolean {
        return oldItem.deckId == newItem.deckId
    }
    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Deck, newItem: Deck): Boolean {
        return oldItem == newItem
    }
}

class DecksListener(val clickListener: (deckId: Deck) -> Unit) {
    fun onClick(deck: Deck) = clickListener(deck)
}