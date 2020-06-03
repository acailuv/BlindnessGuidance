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

    fun speak() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)

        _speechRecognitionIntent.value = intent
    }

    fun resetFlag() {
        _speechRecognitionIntent.value = null
    }
}