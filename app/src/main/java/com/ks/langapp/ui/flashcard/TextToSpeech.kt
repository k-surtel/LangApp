package com.ks.langapp.ui.flashcard

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.*

class TextToSpeech(context: Context) : TextToSpeech.OnInitListener {

    private val textToSpeech = TextToSpeech(context, this)

    override fun onInit(status: Int) {
        textToSpeech.language = Locale("rus")
        //val locales = Locale.getAvailableLocales().distinctBy { it.language }
    }

    fun speak(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, text)
    }
}