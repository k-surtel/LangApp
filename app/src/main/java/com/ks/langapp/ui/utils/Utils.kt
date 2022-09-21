package com.ks.langapp.ui.utils

import java.util.concurrent.TimeUnit
import kotlin.math.floor

fun convertTimeFromTimestamp(timestamp: Long): String {
    var ss = TimeUnit.MILLISECONDS.toSeconds(timestamp).toDouble()
    val hh = floor(ss / 3600)
    if (hh > 0) ss -= (hh * 3600)
    val mm = floor(ss / 60)
    if (mm > 0) ss -= (mm * 60)

    val h = if (hh < 10) "0${hh.toInt()}" else hh.toInt().toString()
    val m = if (mm < 10) "0${mm.toInt()}" else mm.toInt().toString()
    val s = if (ss < 10) "0${ss.toInt()}" else ss.toInt().toString()

    return "$h:$m:$s"
}