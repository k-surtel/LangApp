package com.ks.langapp.ui.cards

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ks.langapp.R
import com.ks.langapp.database.LangDatabase
import com.ks.langapp.databinding.FragmentCardsBinding
import com.ks.langapp.ui.adapters.CardsAdapter
import com.ks.langapp.ui.adapters.CardsListener
import com.ks.langapp.ui.utils.navigate
import com.ks.langapp.ui.editdeck.EditDeckFragmentArgs

class CardsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View {

        val binding: FragmentCardsBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_cards, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = LangDatabase.getInstance(application).langDatabaseDao
        val arguments = EditDeckFragmentArgs.fromBundle(requireArguments())

        val viewModelFactory = CardsViewModelFactory(dataSource, application)
        val viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[CardsViewModel::class.java]

        viewModel.processArguments(arguments.deckId)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val adapter = CardsAdapter(CardsListener { card -> viewModel.onCardClick(card) })
        viewModel.cards.observe(viewLifecycleOwner) { it?.let { adapter.submitList(it) } }
        binding.cards.adapter = adapter

        viewModel.navigateToEditCard.observe(viewLifecycleOwner) { card ->
            card?.let {
                navigate(CardsFragmentDirections.actionCardsFragmentToEditCardFragment(card.deckId, card.cardId))
                viewModel.onEditCardNavigated()
            }
        }

        viewModel.navigateToFlashcards.observe(viewLifecycleOwner) { ifNavigate ->
            if (ifNavigate) {
                navigate(CardsFragmentDirections.actionCardsFragmentToFlashcardFragment(arguments.deckId))
                viewModel.onFlashcardsNavigated()
            }
        }

        binding.fab.setOnClickListener{
            navigate(CardsFragmentDirections.actionCardsFragmentToEditCardFragment(arguments.deckId, Long.MIN_VALUE))
        }


        return binding.root
    }
}