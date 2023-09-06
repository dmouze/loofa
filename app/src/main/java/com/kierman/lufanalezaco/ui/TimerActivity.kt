package com.kierman.lufanalezaco.ui

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.kierman.lufanalezaco.R
import com.kierman.lufanalezaco.databinding.ActivityTimerBinding
import com.kierman.lufanalezaco.util.TimerService
import com.kierman.lufanalezaco.viewmodel.LufaViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


@Suppress("DEPRECATION")
class TimerActivity : AppCompatActivity() {

    private var recv: String = ""
    private var timerStarted = false
    private lateinit var serviceIntent: Intent
    private var time = 0.0
    private lateinit var binding: ActivityTimerBinding
    private val viewModel by viewModel<LufaViewModel>()
    private var userId = ""
    private var imie = ""
    private var results = ArrayList<String>()


    @SuppressLint("UnspecifiedRegisterReceiverFlag", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_timer
        )

        FirebaseApp.initializeApp(this)
        // Inicjalizacja Firestore
        val firestore = FirebaseFirestore.getInstance()

        getValues()

        val reset = findViewById<ImageView>(R.id.resetButton)
        val changeUser = findViewById<TextView>(R.id.text_change)


        showResults(results, imie)

        changeUser.setOnClickListener {
            val intent = Intent(this, ChoosePlayerActivity::class.java)
            startActivity(intent)
            finish()
        }

        viewModel.putTxt.observe(this) { newReceivedData ->
            if (newReceivedData != null) {
                recv = newReceivedData
                viewModel.txtRead.set(recv)
                Log.d("dostałem", recv)

                if (recv == "a") {
                    Log.d("setuje", "setuje")
                    startTimer()
                } else if (recv == "b") {
                    Log.d("stop", "stop")
                    stopTimer()
                }
            }
        }

        reset.setOnClickListener {
            resetTimer()
        }

        serviceIntent = Intent(applicationContext, TimerService::class.java)
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))

        binding.viewModel = viewModel

    }
    private fun getValues(){
        imie = intent.getStringExtra("user_name")!!
        results = intent.getStringArrayListExtra("user_results")!!
        userId = intent.getStringExtra("user_id")!!
    }

    private fun resetTimer() {
        stopTimer()
        time = 0.0
        binding.timeTV.text = getTimeStringFromDouble(time)
    }

    private fun startTimer() {
        serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
        startService(serviceIntent)
        timerStarted = true


    }

    private fun stopTimer() {
        stopService(serviceIntent)
        timerStarted = false
        val formattedTime = getTimeStringFromDouble(time)
        getValues()
        saveResultToFirestore(userId, formattedTime)
        results.add(formattedTime)
        showResults(results,imie)
    }



    private fun saveResultToFirestore(userId: String, formattedTime: String) {
        val firestore = FirebaseFirestore.getInstance()
        val userDocument = firestore.collection("menele").document(userId)


        // Użyj funkcji FieldValue.arrayUnion, aby dodać nowy czas do listy wyników
        val newTimeData =  FieldValue.arrayUnion(formattedTime)

        // Aktualizuj listę wyników w dokumencie użytkownika
        userDocument.update("czas", newTimeData)
            .addOnSuccessListener {
                Toast.makeText(this, "Zapisano", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Błąd podczas zapisywania wyniku do Firestore: ${e.message}")
            }
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val newTime = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
            time = newTime
            binding.timeTV.text = getTimeStringFromDouble(time)
        }
    }

    private fun getTimeStringFromDouble(time: Double): String {
        val seconds = time.toInt()
        val milliseconds = ((time - seconds) * 1000).toInt()
        return makeTimeString(seconds, milliseconds)
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    private fun showResults(results: ArrayList<String>?, imie: String?) {

        val currentUser = findViewById<TextView>(R.id.current_user)
        val recordView = findViewById<TextView>(R.id.recordView)
        val lastTryView = findViewById<TextView>(R.id.lastTryView)
        currentUser.text = "$imie"


        val bestResult = results
            ?.filter { it.isNotBlank() } // Usuń puste ciągi znaków
            ?.minByOrNull { time ->
                val parts = time.split(":")
                val seconds = parts[0].toInt() * 60 + parts[1].toInt()
                seconds
            }

        if (bestResult != null) {
            recordView.text = bestResult
        } else {
            // Obsłuż przypadek, gdy nie ma żadnych wyników
            recordView.text = "Brak"
        }


        val latestResult = results?.takeIf { it.isNotEmpty() }?.lastOrNull()

        if (latestResult != null) {
            lastTryView.text = latestResult
        } else {
            lastTryView.text = "Brak"
        }
    }

    private fun makeTimeString(sec: Int, milisec: Int): String =
        String.format("%02d:%03d", sec, milisec)
}