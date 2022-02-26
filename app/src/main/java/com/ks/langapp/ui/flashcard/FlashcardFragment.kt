package com.ks.langapp.ui.flashcard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ks.langapp.R
import com.ks.langapp.database.LangDatabase
import com.ks.langapp.databinding.FragmentFlashcardBinding

class FlashcardFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding: FragmentFlashcardBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_flashcard, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = LangDatabase.getInstance(application).langDatabaseDao
        val arguments = FlashcardFragmentArgs.fromBundle(requireArguments())

        val viewModelFactory = FlashcardViewModelFactory(dataSource, application)
        val viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[FlashcardViewModel::class.java]

        viewModel.processArguments(arguments.deckId)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.cards.observe(viewLifecycleOwner) { cards ->
            cards?.let {
                viewModel.setFlashcard()
            }
        }

        return binding.root
    }
}