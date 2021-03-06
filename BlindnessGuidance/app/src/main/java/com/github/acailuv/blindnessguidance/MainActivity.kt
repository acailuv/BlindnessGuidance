package com.github.acailuv.blindnessguidance

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.acailuv.blindnessguidance.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var mTTS: TextToSpeech
    lateinit var mTTSIndonesian: TextToSpeech
    lateinit var speechLanguage: String

    lateinit var vibrator: Vibrator

    private val NO_ARGS = "NO_ARGS"

    private fun navigate(args: String, language: String) {

        // No destination specified.
        if (args == NO_ARGS) {
            when (language) {
                "en" -> mTTS.speak("Destination not specified. Please specify your destination and try again.", TextToSpeech.QUEUE_FLUSH, null)
                "id" -> mTTSIndonesian.speak("Tempat tujuan belum ditentukan. Mohon tentukan tempat tujuan dan coba lagi.", TextToSpeech.QUEUE_FLUSH, null)
            }
            return
        }

        when (language) {
            "en" -> mTTS.speak("Understood. Navigating to "+args, TextToSpeech.QUEUE_FLUSH, null)
            "id" -> mTTSIndonesian.speak("Oke. Bernavigasi ke "+args, TextToSpeech.QUEUE_FLUSH, null)
        }
        args.replace(" ", "+")
        val gmmIntentUri = Uri.parse("google.navigation:q="+args+"&mode=w")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)

            val handler = Handler()
            handler.postDelayed(
                Runnable {
                    // Reopen This Activity
                    val thisIntent = baseContext.packageManager.getLaunchIntentForPackage(
                        baseContext.packageName)
                    startActivity(thisIntent)

                    handler.postDelayed(
                        Runnable {
                            when (language) {
                                "en" -> mTTS.speak("If you do not hear any directions, the place you just mentioned might be invalid.", TextToSpeech.QUEUE_FLUSH, null)
                                "id" -> mTTSIndonesian.speak("Kalau tidak ada arahan dari Google Maps, tempat yang kamu pilih bisa saja tidak valid.", TextToSpeech.QUEUE_FLUSH, null)
                            }

                        },
                        5000
                    )
                },
                10000
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Data Binding
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        val viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        binding.viewModel = viewModel

        // Initiating vibrate service
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // Text to Speech
        mTTS = TextToSpeech(this, TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = mTTS.setLanguage(Locale.ENGLISH)
                mTTS.setPitch(1.1f)
                mTTS.setSpeechRate(1.0f)

                if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported.")
                }
            } else {
                Log.e("TTS", "TTS Init failed.")
            }
        })

        // Text to Speech - Indonesian
        mTTSIndonesian = TextToSpeech(this, TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = mTTSIndonesian.setLanguage(Locale("id", "ID"))
                mTTSIndonesian.setPitch(1.1f)
                mTTSIndonesian.setSpeechRate(1.0f)

                if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported.")
                }
            } else {
                Log.e("TTS", "TTS Init failed.")
            }
        })

        // Live Data Observers
        viewModel.speechRecognizerIntent.observe(this, Observer { status ->
            if (status != null) {
                if (status.resolveActivity(packageManager) != null) {
                    vibrator.vibrate(500)
                    mTTS.stop()
                    mTTSIndonesian.stop()
                    startActivityForResult(status, 10)
                } else {
                    Toast.makeText(this, "Speech Input is not Supported On Your Device.", Toast.LENGTH_LONG).show()
                }
                viewModel.resetFlag()
            }
        })

        viewModel.speechRecognizerLanguage.observe(this, Observer { language ->
            speechLanguage = language
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            10 -> {
                if (resultCode == RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    Log.d("Speech Result", result[0])

                    // Command Parsing/Processing
                    val command = result[0].split(" ", limit=3)
                    Log.d("---------COMMAND_STRING", command.toString())
                    var args = NO_ARGS
                    lateinit var commandParse: String
                    when (command.size) {
                        1 -> commandParse = command[0]
                        2 -> commandParse = command[0] + command[1]
                        else -> {
                            commandParse = command[0] + command[1]
                            args = command[2]
                        }
                    }
                    when (commandParse) {
                        "navigateto"    -> navigate(args, speechLanguage)
                        "goto"          -> navigate(args, speechLanguage)
                        "navigasike"    -> navigate(args, speechLanguage)
                        "menujuke"      -> navigate(args, speechLanguage)
                        "jalanke"       -> navigate(args, speechLanguage)
                        "mengarahke"    -> navigate(args, speechLanguage)
                        "berangkatke"   -> navigate(args, speechLanguage)

                        "help" -> {
                            mTTS.speak("Hello, and welcome to Blindness Guidance. Say 'Navigate to' followed with a destination to get started!", TextToSpeech.QUEUE_FLUSH, null)
                        }

                        "tolong" -> {
                            mTTSIndonesian.speak("Halo, selamat datang ke dalam aplikasi Pemandu Tunanetra. Katakan 'Navigasi Ke' diikuti dengan tempat tujuan untuk memulai!", TextToSpeech.QUEUE_FLUSH, null)
                        }

                        else -> {
                            if (speechLanguage == "en") {
                                mTTS.speak(
                                    "Command not recognized. Say 'Help' to get started!",
                                    TextToSpeech.QUEUE_FLUSH,
                                    null
                                )
                            } else {
                                mTTSIndonesian.speak(
                                    "Perintah tidak dapat dikenali. Katakan 'Tolong' untuk memulai!",
                                    TextToSpeech.QUEUE_FLUSH,
                                    null
                                )
                            }
                        }

                    }
                }
            }
        }
    }

    override fun onDestroy() {
        if (mTTS != null) {
            mTTS.stop()
            mTTS.shutdown()
        }

        if (mTTSIndonesian != null) {
            mTTSIndonesian.stop()
            mTTSIndonesian.shutdown()
        }

        super.onDestroy()
    }
}
