package com.ks.langapp.ui.deck

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ks.langapp.R
import com.ks.langapp.data.database.LangDatabase
import com.ks.langapp.databinding.FragmentDeckBinding
import com.ks.langapp.ui.adapters.CardsAdapter
import com.ks.langapp.ui.adapters.CardsListener
import com.ks.langapp.ui.editdeck.EditDeckFragmentArgs
import com.ks.langapp.ui.utils.navigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeckFragment : Fragment() {

    private val viewModel: DeckViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View {

        val binding: FragmentDeckBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_deck, container, false)

        val arguments = EditDeckFragmentArgs.fromBundle(requireArguments()) //todo

        setHasOptionsMenu(true)

        viewModel.processArguments(arguments.deckId)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val adapter = CardsAdapter(CardsListener { card -> viewModel.onCardClick(card) })
        lifecycleScope.launch { viewModel.cards.collectLatest { adapter.submitList(it) } }
        binding.cards.adapter = adapter

        viewModel.navigateToEditCard.observe(viewLifecycleOwner) { card ->
            card?.let {
                navigate(DeckFragmentDirections.actionDeckFragmentToEditCardFragment(card.deckId, card.cardId))
                viewModel.onEditCardNavigated()
            }
        }

        viewModel.navigateToFlashcards.observe(viewLifecycleOwner) { ifNavigate ->
            if (ifNavigate) {
                navigate(DeckFragmentDirections.actionDeckFragmentToFlashcardFragment(arguments.deckId))
                viewModel.onFlashcardsNavigated()
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
            viewModel.deckId?.let {
                navigate(DeckFragmentDirections
                    .actionDeckFragmentToEditCardFragment(it, Long.MIN_VALUE))
            }
            true
        }

        R.id.action_edit -> {
            viewModel.deckId?.let {
                navigate(DeckFragmentDirections
                    .actionDeckFragmentToEditDeckFragment(it))
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}