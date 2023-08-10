package com.kierman.lufanalezaco.ui

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.kierman.lufanalezaco.R

@Suppress("DEPRECATION")
class SplashScreen : AppCompatActivity() {
    private val splashScreenDuration: Long = 7000 // Czas trwania Splash Screenu w milisekundach (8,5 sekundy)
    private lateinit var handler: Handler
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        setContentView(R.layout.activity_splashscreen)

        val introLogo = findViewById<ImageView>(R.id.intro_icon)

        mediaPlayer = MediaPlayer.create(this, R.raw.deszcz)
        mediaPlayer.start()
        introLogo.alpha = 0f
        introLogo.animate().setDuration(splashScreenDuration / 2).alpha(1f).withEndAction {
            handler = Handler()
            handler.postDelayed({
                val nextActivity = MainActivity::class.java
                val intent = Intent(this, nextActivity)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
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
