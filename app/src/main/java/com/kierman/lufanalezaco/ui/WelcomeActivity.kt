package com.kierman.lufanalezaco.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.kierman.lufanalezaco.R
import com.kierman.lufanalezaco.databinding.ActivityWelcomeBinding
import com.kierman.lufanalezaco.viewmodel.LufaViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class WelcomeActivity : AppCompatActivity() {

    private val viewModel by viewModel<LufaViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        val binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_welcome
        ) as ActivityWelcomeBinding

        val zapalButton = findViewById<Button>(R.id.button)
        val zgasButton = findViewById<Button>(R.id.button2)

        zapalButton.setOnClickListener {
            viewModel.zapalLed()
        }

        zgasButton.setOnClickListener {
            viewModel.zgasLed()
        }

        binding.viewModel = viewModel

    }
}