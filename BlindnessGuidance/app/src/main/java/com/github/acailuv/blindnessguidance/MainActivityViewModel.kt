package com.github.acailuv.blindnessguidance

import android.content.Intent
import android.speech.RecognizerIntent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class MainActivityViewModel: ViewModel() {

    private var _speechRecognitionIntent = MutableLiveData<Intent>()
    val speechRecognizerIntent: LiveData<Intent>
        get() = _speechRecognitionIntent

    private var _speechRecognitionLanguage = MutableLiveData<String>()
    val speechRecognizerLanguage: LiveData<String>
        get() = _speechRecognitionLanguage

    fun speak() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)

        _speechRecognitionIntent.value = intent
        _speechRecognitionLanguage.value = "en"
    }

    fun speakIndonesian(): Boolean {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID")

        _speechRecognitionIntent.value = intent
        _speechRecognitionLanguage.value = "id"
        return true
    }

    fun resetFlag() {
        _speechRecognitionIntent.value = null
    }
}