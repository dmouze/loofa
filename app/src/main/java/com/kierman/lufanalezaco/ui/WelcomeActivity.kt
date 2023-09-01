package com.kierman.lufanalezaco.ui

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.kierman.lufanalezaco.R
import com.kierman.lufanalezaco.databinding.ActivityWelcomeBinding
import com.kierman.lufanalezaco.util.TimerService
import com.kierman.lufanalezaco.viewmodel.FirebaseRepo
import com.kierman.lufanalezaco.viewmodel.LufaViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


@Suppress("DEPRECATION")
class WelcomeActivity : AppCompatActivity() {

    private var recv: String = ""
    private var timerStarted = false
    private lateinit var serviceIntent: Intent
    private var time = 0.0
    private lateinit var binding: ActivityWelcomeBinding
    private val viewModel by viewModel<LufaViewModel>()
    private val firebaseRepo = FirebaseRepo()


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_welcome
        )

        binding.userList.layoutManager = LinearLayoutManager(this)

        val reset = findViewById<Button>(R.id.reset)

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
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            time = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
            binding.timeTV.text = getTimeStringFromDouble(time)
        }
    }

    private fun getTimeStringFromDouble(time: Double): String {
        val seconds = time.toInt()
        val milliseconds = ((time - seconds) * 1000).toInt()
        return makeTimeString(seconds, milliseconds)
    }

    private fun makeTimeString(sec: Int, milisec: Int): String =
        String.format("%02d:%03d", sec, milisec)
}