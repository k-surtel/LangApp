package com.ks.langapp.ui.flashcard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.ks.langapp.R
import com.ks.langapp.data.database.LangDatabase
import com.ks.langapp.databinding.FragmentFlashcardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FlashcardFragment : Fragment() {

    private val viewModel: FlashcardViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding: FragmentFlashcardBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_flashcard, container, false)

        val arguments = FlashcardFragmentArgs.fromBundle(requireArguments())
        viewModel.processArguments(arguments.deckId)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }
}