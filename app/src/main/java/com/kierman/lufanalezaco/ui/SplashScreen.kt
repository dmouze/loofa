package com.kierman.lufanalezaco.ui

import android.animation.ObjectAnimator
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.kierman.lufanalezaco.R

@Suppress("DEPRECATION")
class SplashScreen : AppCompatActivity() {
    private val splashScreenDuration: Long = 7000
    private lateinit var handler: Handler
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE
        supportActionBar?.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splashscreen)

        val introLogo = findViewById<ImageView>(R.id.intro_icon)

        mediaPlayer = MediaPlayer.create(this, R.raw.deszcz)
        mediaPlayer.start()
        introLogo.alpha = 0f
        ObjectAnimator.ofFloat(
            introLogo,
            "scaleX",
            0.5f,
            1f
        ).apply {
            duration = 7000
            ObjectAnimator.ofFloat(
                introLogo,
                "scaleY",
                0.5f,
                1f
            ).apply {
                duration = 7000
            }.start()
        }.start()
        introLogo.animate().setDuration(splashScreenDuration / 2).alpha(1f).withEndAction {
            handler = Handler()
            handler.postDelayed({
                val nextActivity = ConnectActivity::class.java
                val intent = Intent(this, nextActivity)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.slide_out_right)

                finish()
            }, splashScreenDuration / 2)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::handler.isInitialized) {
            handler.removeCallbacksAndMessages(null)
            mediaPlayer.release()
        }
    }
}
