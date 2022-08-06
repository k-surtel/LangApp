package com.ks.langapp.ui.stats

import android.util.Log
import androidx.lifecycle.*
import com.ks.langapp.data.repository.LangRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.floor


@HiltViewModel
class StatsViewModel @Inject constructor(
    private val repository: LangRepository
) : ViewModel() {

    fun loadDeckStats() {
        viewModelScope.launch {
            val deckStats = repository.getDeckStats()

            for (stat in deckStats) {

                val time = stat.time

                var ss = TimeUnit.MILLISECONDS.toSeconds(time).toDouble()
                val hh = floor(ss / 3600)
                if (hh > 0) ss -= (hh * 3600)
                val mm = floor(ss / 60)
                if (mm > 0) ss -= (mm * 60)


                Log.d("LANGUS", "time: ${stat.time}")
                Log.d("LANGUS", "DECKID: ${stat.deckId}, DATE: ${stat.date}, " +
                        "TIME: ${hh.toInt()}:${mm.toInt()}:${ss.toInt()}")
            }
        }
    }
}