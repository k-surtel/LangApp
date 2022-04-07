package com.ks.langapp.ui.deck

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ks.langapp.R
import com.ks.langapp.database.LangDatabase
import com.ks.langapp.databinding.FragmentDeckBinding
import com.ks.langapp.ui.adapters.CardsAdapter
import com.ks.langapp.ui.adapters.CardsListener
import com.ks.langapp.ui.editdeck.EditDeckFragmentArgs
import com.ks.langapp.ui.utils.navigate


class DeckFragment : Fragment() {

    private lateinit var viewModel: DeckViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View {

        val binding: FragmentDeckBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_deck, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = LangDatabase.getInstance(application).langDatabaseDao
        val arguments = EditDeckFragmentArgs.fromBundle(requireArguments())

        val viewModelFactory = DeckViewModelFactory(dataSource, application)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[DeckViewModel::class.java]

        setHasOptionsMenu(true)

        viewModel.processArguments(arguments.deckId)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val adapter = CardsAdapter(CardsListener { card -> viewModel.onCardClick(card) })
        viewModel.cards.observe(viewLifecycleOwner) { it?.let { adapter.submitList(it) } }
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
                    .actionDeckFragmentToEditCardFragment(viewModel.deckId!!, Long.MIN_VALUE))
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}