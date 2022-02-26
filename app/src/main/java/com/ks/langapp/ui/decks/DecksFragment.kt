package com.ks.langapp.ui.decks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ks.langapp.R
import com.ks.langapp.database.LangDatabase
import com.ks.langapp.databinding.FragmentDecksBinding
import com.ks.langapp.ui.utils.navigate

class DecksFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View {

        val binding: FragmentDecksBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_decks, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = LangDatabase.getInstance(application).langDatabaseDao

        val viewModelFactory = DecksViewModelFactory(dataSource, application)
        val viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[DecksViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val adapter = DecksAdapter(DecksListener { itemClicked, deckId ->
            when(itemClicked) {
                0 -> viewModel.onDeckClick(deckId)
                1 -> viewModel.onEditButtonClick(deckId)
            }
        })
        viewModel.decks.observe(viewLifecycleOwner) { it?.let { adapter.submitList(it) } }
        binding.decks.adapter = adapter

        viewModel.navigateToCards.observe(viewLifecycleOwner) { deckId ->
            deckId?.let {
                if (deckId != Long.MIN_VALUE) {
                    navigate(DecksFragmentDirections.actionDecksFragmentToCardsFragment(deckId))
                    viewModel.onCardsListNavigated()
                }
            }
        }

        viewModel.navigateToEditDeck.observe(viewLifecycleOwner) { deckId ->
            deckId?.let {
                if (deckId != Long.MIN_VALUE) {
                    navigate(DecksFragmentDirections.actionDecksFragmentToEditDeckFragment(deckId))
                    viewModel.onEditDeckNavigated()
                }
            }
        }

        viewModel.navigateToImport.observe(viewLifecycleOwner) { ifNavigate ->
            if (ifNavigate) {
                navigate(DecksFragmentDirections.actionDecksFragmentToImportFragment())
                viewModel.onImportNavigated()
            }
        }

        binding.fab.setOnClickListener{
            navigate(DecksFragmentDirections.actionDecksFragmentToEditDeckFragment(Long.MIN_VALUE))
        }

        return binding.root
    }
}