/*
 * Copyright (c) Mya Than Htet 2021.
 */

package com.mth.assistantmya

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.markushi.ui.CircleButton
import com.mth.assistantmya.adapter.ChatRecyclerAdapter
import com.mth.assistantmya.model.BotMessage
import com.mth.assistantmya.model.ChatModel
import com.mth.assistantmya.network.RetrofitApi
import com.mth.assistantmya.network.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var chatsRecyclerViwe: RecyclerView
    private lateinit var speechBtn: CircleButton
    private lateinit var speechStatusTv: TextView
    private lateinit var messageModelModalArrayList: ArrayList<ChatModel>
    private lateinit var chatRecyclerAdapter: ChatRecyclerAdapter

    private val RecordAudioRequestCode: Int = 1;
    private lateinit var speechRecognizer: SpeechRecognizer


    private val USER_KEY = "user"
    private val BOT_KEY = "bot"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            checkPermission();
        }


        supportActionBar?.apply {
            val titleText = "AssistantMya"
            val titleTextColor = ForegroundColorSpan(Color.BLACK)
            val spannString = SpannableString(titleText)
            spannString.setSpan(
                titleTextColor, 0, titleText.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            title = spannString
        }



        chatsRecyclerViwe = findViewById(R.id.idRVChats)
        speechBtn = findViewById(R.id.tap_to_speech_btn)
        speechStatusTv = findViewById(R.id.listening_status)
        messageModelModalArrayList = arrayListOf<ChatModel>()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {}
            override fun onBeginningOfSpeech() {
                speechStatusTv.text = ""
                speechStatusTv.hint = "Listening..."
            }

            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray) {}
            override fun onEndOfSpeech() {}
            override fun onError(i: Int) {}
            override fun onResults(bundle: Bundle) {
                speechBtn.setColor(Color.parseColor("#99CC00"))
                val data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                speechStatusTv.text = data!![0]
                sendMessage(data[0])
            }

            override fun onPartialResults(bundle: Bundle) {}
            override fun onEvent(i: Int, bundle: Bundle) {}
        })

        speechBtn.setOnTouchListener { _, motionEvent ->

            if (motionEvent.action == MotionEvent.ACTION_UP) {
                speechRecognizer.stopListening()
            }
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                speechBtn.setColor(Color.RED)
                speechRecognizer.startListening(speechRecognizerIntent)
            }

            false
        }

        chatRecyclerAdapter = ChatRecyclerAdapter(messageModelModalArrayList, this)
        val linearLayoutManager =
            LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
        chatsRecyclerViwe.layoutManager = linearLayoutManager
        chatsRecyclerViwe.adapter = chatRecyclerAdapter


    }


    private fun sendMessage(userMsg: String) {
        messageModelModalArrayList.add(ChatModel(userMsg, USER_KEY))
        chatRecyclerAdapter.notifyDataSetChanged()

        val url =
            "http://api.brainshop.ai/get?bid=162431&key=k3wHupdOOCVidgiE&uid=[uid]&msg=$userMsg"


        val retrofit = ServiceBuilder.buildService(RetrofitApi::class.java)
        retrofit.getMessage(url)
            .enqueue(
                object : Callback<BotMessage> {

                    override fun onFailure(call: Call<BotMessage>, t: Throwable) {
                        Log.i(MainActivity::class.simpleName, "on FAILURE!!!!$t")
                    }


                    override fun onResponse(
                        call: Call<BotMessage>,
                        response: Response<BotMessage>
                    ) {
                        if (response.isSuccessful) {
                            val model: BotMessage = response.body()!!


                            messageModelModalArrayList.add(ChatModel(model.cnt, BOT_KEY))
                            chatRecyclerAdapter.notifyDataSetChanged()

                            Log.i(MainActivity::class.simpleName, "${model.cnt}")

                        }

                    }
                }
            )

    }


    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RecordAudioRequestCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RecordAudioRequestCode && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) Toast.makeText(
                this,
                "Permission Granted",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}