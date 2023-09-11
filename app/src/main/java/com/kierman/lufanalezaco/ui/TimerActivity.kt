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
import com.google.firebase.database.*
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
    private var results = ArrayList<Double>()
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
            if (newReceivedData != null)  {
                    recv = newReceivedData
                    viewModel.txtRead.set(recv)

                    if (recv == "a") {
                        startTimer()
                        FirebaseApp.initializeApp(this)
                    } else if (recv == "b") {
                        getValues()
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


    private fun getValues() {
        imie = intent.getStringExtra("user_name")!!
        userId = intent.getStringExtra("user_id")!!

        // Odczytaj ArrayList<Double> zamiast DoubleArray
        val resultArrayList = intent.getSerializableExtra("user_results") as ArrayList<Double>
        results = resultArrayList
    }

    private fun resetTimer() {
        stopTimer()
        time = 0.0
        binding.timeTV.text = getTimeStringFromDouble(time)
    }

    private fun startTimer() {
        serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
        startService(serviceIntent)
        getValues()
        timerStarted = true
    }

    private fun stopTimer() {
        stopService(serviceIntent)
        timerStarted = false
        val formattedTime = time
        if (formattedTime > 0.0) {
            getValues()
            saveResultToRealtimeDatabase(
                userId,
                formattedTime
            ) // Zaktualizowano na Firebase Realtime Database
            results.add(formattedTime)
            // Wyświetl wyniki
            showResults(results, imie)
        }
    }

    private fun saveResultToRealtimeDatabase(userId: String, formattedTime: Double) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("menele")
        val userReference = databaseReference.child(userId)

        if (formattedTime > 0.0) {
            userReference.child("czas").push().setValue(formattedTime)
                .addOnSuccessListener {
                    Toast.makeText(this, "Zapisano", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Błąd podczas zapisywania wyniku do Realtime Database: ${e.message}")
                }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showResults(results: ArrayList<Double>?, imie: String?) {
        val currentUser = findViewById<TextView>(R.id.current_user)
        val recordView = findViewById<TextView>(R.id.recordView)
        val lastTryView = findViewById<TextView>(R.id.lastTryView)
        val timer = findViewById<TextView>(R.id.timeTV)
        currentUser.text = "$imie"

        // Wybierz ostatni wynik
        val latestResult = results?.takeIf { it.isNotEmpty() }?.lastOrNull()

        val showLatestResult = latestResult?.let { getTimeStringFromDouble(it) }

        if (latestResult != null) {
            lastTryView.text = showLatestResult?.let { formatTime(it) }
            timer.text = showLatestResult?.let { formatTime(it) }
        } else {
            lastTryView.text = "Brak"
        }

        // Wybierz najlepszy wynik (najbliższy 0)
        val bestResult = results
            ?.filter { it > 0.0 }
            ?.minByOrNull { time ->
                val seconds = (time * 60).toInt()
                val milliseconds = ((time - seconds / 60.0) * 1000).toInt()
                seconds * 1000 + milliseconds
            }
        val showBestTime = bestResult?.let { getTimeStringFromDouble(it) }

        if (bestResult != null) {
            recordView.text = showBestTime?.let { formatTime(it) }
        } else {
            // Obsłuż przypadek, gdy nie ma żadnych wyników
            recordView.text = "Brak"
        }
    }

    private fun formatTime(time: String): String {
        val parts = time.split(":")
        val seconds = parts[0].toInt()
        val milliseconds = parts[1].toInt()
        return String.format("%02d:%03d", seconds, milliseconds)
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
        return String.format("%02d:%03d", seconds, milliseconds)
    }
}
