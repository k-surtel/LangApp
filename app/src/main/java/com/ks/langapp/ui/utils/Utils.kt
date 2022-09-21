package com.ks.langapp.ui.utils

import java.util.concurrent.TimeUnit
import kotlin.math.floor

fun convertTimeFromTimestamp(timestamp: Long): String {
    var ss = TimeUnit.MILLISECONDS.toSeconds(timestamp).toDouble()
    val hh = floor(ss / 3600)
    if (hh > 0) ss -= (hh * 3600)
    val mm = floor(ss / 60)
    if (mm > 0) ss -= (mm * 60)

    return "${hh.toInt()}:${mm.toInt()}:${ss.toInt()}"
}