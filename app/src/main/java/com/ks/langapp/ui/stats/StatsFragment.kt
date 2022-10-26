package com.ks.langapp.ui.stats

import android.os.Bundle
import android.view.*
import android.widget.HorizontalScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ks.langapp.R
import com.ks.langapp.databinding.FragmentStatsBinding
import com.ks.simpledatechart.DataEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StatsFragment : Fragment() {

    private val viewModel: StatsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View {

        val binding: FragmentStatsBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_stats, container, false)

        binding.lifecycleOwner = this

        viewModel.loadDeckStats()

        lifecycleScope.launch {
            viewModel.deckStats.collectLatest { deckStats ->
                val entries = mutableListOf<DataEntry>()
                for (stat in deckStats) entries.add(DataEntry(stat.date, stat.cardsReviewed.toDouble()))
                binding.barChart.setData(entries)
            }
        }

        lifecycleScope.launch {
            delay(100L)
            binding.barChartScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
        }

        return binding.root
    }
}