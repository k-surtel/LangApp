package com.ks.langapp.data.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.*

class TextToSpeech(context: Context) : TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech? = null

    private var language: Locale = Locale("en")

    init { textToSpeech = TextToSpeech(context, this) }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            try {
                textToSpeech?.language = language
            } catch (e: MissingResourceException) {
                Log.e("TextToSpeech", e.toString())
            }
        } // todo else error
    }

    fun setLanguage(language: String) {
        this.language = Locale(language)
    }

    fun speak(text: String) {
        textToSpeech?.let {
            if (it.voice.locale != language) updateLanguage()
            it.speak(text, TextToSpeech.QUEUE_FLUSH, null, text)
        }
    }

    private fun updateLanguage() {
        textToSpeech?.language = language
    }
}