package com.ks.langapp.ui.deck

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.ks.langapp.R
import com.ks.langapp.data.database.LangDatabase
import com.ks.langapp.databinding.FragmentDeckBinding
import com.ks.langapp.ui.adapters.CardsAdapter
import com.ks.langapp.ui.adapters.CardsListener
import com.ks.langapp.ui.editdeck.EditDeckFragmentArgs
import com.ks.langapp.ui.utils.navigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeckFragment : Fragment() {

    private val viewModel: DeckViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View {

        val binding: FragmentDeckBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_deck, container, false)

        val arguments = DeckFragmentArgs.fromBundle(requireArguments())
        viewModel.processArguments(arguments.deckId)

        setHasOptionsMenu(true)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val adapter = CardsAdapter(CardsListener { card ->
            viewModel.deck.value?.let {
                viewModel.editedCard = card
                navigate(DeckFragmentDirections.actionDeckFragmentToEditCardFragment(it.deckId, card.cardId))
            }
        })
        lifecycleScope.launch { viewModel.cards.collectLatest {
            if (viewModel.deck.value!= null && it.size != viewModel.deck.value!!.cardsCount)
                viewModel.checkForUpdates(it)
            adapter.submitList(it)
        } }
        binding.cards.adapter = adapter

        binding.editButton.setOnClickListener {
            viewModel.deck.value?.let {
                navigate(DeckFragmentDirections.actionDeckFragmentToEditDeckFragment(it.deckId, true))
            }
        }

        binding.learnButton.setOnClickListener {
            viewModel.deck.value?.let {
                navigate(DeckFragmentDirections.actionDeckFragmentToFlashcardFragment(it.deckId))
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.showUndoForCardDeletion.collectLatest { card ->
                Snackbar.make(requireView(), R.string.card_deleted, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo) { viewModel.undoCardDeletion(card) }
                    .show()
            }
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_toolbar2, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_add -> {
            viewModel.deck.value?.let {
                navigate(DeckFragmentDirections
                    .actionDeckFragmentToEditCardFragment(it.deckId, Long.MIN_VALUE))
            }
            true
        }

        R.id.action_edit -> {
            viewModel.deck.value?.let {
                navigate(DeckFragmentDirections
                    .actionDeckFragmentToEditDeckFragment(it.deckId, true))
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}