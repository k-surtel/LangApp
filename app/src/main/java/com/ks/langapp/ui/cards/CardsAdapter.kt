package com.ks.langapp.ui.cards

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ks.langapp.database.entities.Card
import com.ks.langapp.databinding.ItemCardBinding

class CardsAdapter(private val clickListener: CardsListener) :
    ListAdapter<Card, CardsAdapter.ViewHolder>(EntriesDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: ItemCardBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(card: Card, clickListener: CardsListener) {
            binding.card = card
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCardBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class EntriesDiffCallback : DiffUtil.ItemCallback<Card>() {
    override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean {
        return oldItem.cardId == newItem.cardId
    }
    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Card, newItem: Card): Boolean {
        return oldItem == newItem
    }
}

class CardsListener(val clickListener: (card: Card) -> Unit) {
    fun onClick(card: Card) = clickListener(card)
}