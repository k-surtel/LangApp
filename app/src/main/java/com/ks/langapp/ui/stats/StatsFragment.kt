package com.ks.langapp.ui.stats

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ks.langapp.R
import com.ks.langapp.databinding.FragmentStatsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatsFragment : Fragment() {

    private val viewModel: StatsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View {

        val binding: FragmentStatsBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_stats, container, false)

//        val arguments = DeckStatsFragmentArgs.fromBundle(requireArguments())
//        viewModel.processArguments(arguments.deckId)

        //binding.viewModel = viewModel
        binding.lifecycleOwner = this


        viewModel.loadDeckStats()

        return binding.root
    }
}