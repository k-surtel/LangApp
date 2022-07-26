package com.ks.langapp.ui.decks

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ks.langapp.R
import com.ks.langapp.database.LangDatabase
import com.ks.langapp.database.entities.Deck
import com.ks.langapp.databinding.FragmentDecksBinding
import com.ks.langapp.ui.adapters.DecksAdapter
import com.ks.langapp.ui.adapters.DecksListener
import com.ks.langapp.ui.utils.navigate


class DecksFragment : Fragment() {

    private lateinit var binding: FragmentDecksBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_decks, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = LangDatabase.getInstance(application).langDatabaseDao

        val viewModelFactory = DecksViewModelFactory(dataSource, application)
        val viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[DecksViewModel::class.java]

        setHasOptionsMenu(true)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val adapter = DecksAdapter(DecksListener { deck ->
            onDeckClick(deck)
        })

        viewModel.decks.observe(viewLifecycleOwner) { it?.let { adapter.submitList(it) } }
        binding.decks.adapter = adapter

        viewModel.navigateToCards.observe(viewLifecycleOwner) { deckId ->
            deckId?.let {
                if (deckId != Long.MIN_VALUE) {
                    navigate(DecksFragmentDirections.actionDecksFragmentToDeckFragment(deckId))
                    viewModel.onDeckNavigated()
                }
            }
        }

        viewModel.navigateToEditDeck.observe(viewLifecycleOwner) { deckId ->
            deckId?.let {
                if (deckId != Long.MIN_VALUE) {
                    navigate(DecksFragmentDirections.actionDecksFragmentToFlashcardFragment(deckId))
                    viewModel.onReviewNavigated()
                }
            }
        }

        return binding.root
    }

    private fun onDeckClick(deck: Deck) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(deck.name)

        builder.setItems(
            R.array.dialog_deck_actions
        ) { dialog, which ->
            Log.d("LANG", which.toString())
        }

        // create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_add -> {
            navigate(DecksFragmentDirections.actionDecksFragmentToEditDeckFragment(Long.MIN_VALUE))
            true
        }
        R.id.action_import -> {
            navigate(DecksFragmentDirections.actionDecksFragmentToImportFragment())
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}