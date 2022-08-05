package com.ks.langapp.ui.deck_stats

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ks.langapp.R
import com.ks.langapp.databinding.FragmentDeckStatsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeckStatsFragment : Fragment() {

    private val viewModel: DeckStatsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View {

        val binding: FragmentDeckStatsBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_deck_stats, container, false)

//        val arguments = DeckStatsFragmentArgs.fromBundle(requireArguments())
//        viewModel.processArguments(arguments.deckId)

        //binding.viewModel = viewModel
        binding.lifecycleOwner = this


        viewModel.loadDeckStats()

        return binding.root
    }
}