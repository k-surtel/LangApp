package com.ks.langapp.ui.stats

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ks.langapp.R
import com.ks.langapp.databinding.FragmentStatsBinding
import com.ks.langapp.ui.stats.charts.DailyHeatMap
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

        ///////////


        val barView = binding.barView
        barView.setBottomTextList(arrayListOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"))
        barView.setDataList(arrayListOf(1, 2, 4, 2, 6, 1, 3, 1, 7, 7, 8), 8)

        //

        //binding.customView.stats = listOf()

//        binding.customView.setOnClickListener {
//            Toast.makeText(requireContext(), "xD", Toast.LENGTH_SHORT).show()
//        }

//        binding.dailyHeatMapLayout.addView(DailyHeatMap(requireContext()), 600, 600)
//
//        binding.dailyHeatMapLayout.get(0).setOnClickListener {
//            Toast.makeText(requireContext(), "...", Toast.LENGTH_SHORT).show()
//        }

//        binding.dailyHeatMapLayout.post {
//            binding.dailyHeatMapLayout.fullScroll(View.FOCUS_RIGHT)
//        }

        return binding.root
    }
}