@file:Suppress("DEPRECATION")

package com.kierman.lufanalezaco.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.kierman.lufanalezaco.R
import com.kierman.lufanalezaco.databinding.ActivityWelcomeBinding

class CreateOrChooseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_create_or_choose
        )
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

}