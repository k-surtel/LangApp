package com.ks.langapp.ui.listOfDecks

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ks.langapp.R
import com.ks.langapp.data.database.LangDatabase
import com.ks.langapp.data.database.entities.Deck
import com.ks.langapp.databinding.FragmentListOfDecksBinding
import com.ks.langapp.ui.adapters.DecksAdapter
import com.ks.langapp.ui.adapters.DecksListener
import com.ks.langapp.ui.utils.navigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListOfDecksFragment : Fragment() {

    private lateinit var binding: FragmentListOfDecksBinding
    private val viewModel: ListOfDecksViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_list_of_decks, container, false)

        setHasOptionsMenu(true)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val adapter = DecksAdapter(DecksListener { deck ->
            onDeckClick(deck)
        })
        lifecycleScope.launch { viewModel.decks.collectLatest { adapter.submitList(it) } }
        binding.decks.adapter = adapter

        return binding.root
    }

    private fun onDeckClick(deck: Deck) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(deck.name)
        builder.setItems(
            R.array.dialog_deck_actions
        ) { dialog, which ->

            when (which) {
                0 -> navigate(ListOfDecksFragmentDirections
                    .actionListOfDecksFragmentToDeckFragment(deck.deckId))

                1 -> navigate(ListOfDecksFragmentDirections
                    .actionListOfDecksFragmentToFlashcardFragment(deck.deckId))

                2 -> navigate(ListOfDecksFragmentDirections
                    .actionListOfDecksFragmentToEditDeckFragment(deck.deckId))
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_add -> {
            navigate(ListOfDecksFragmentDirections.actionListOfDecksFragmentToEditDeckFragment(Long.MIN_VALUE))
            true
        }
        R.id.action_import -> {
            navigate(ListOfDecksFragmentDirections.actionListOfDecksFragmentToImportFragment())
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}