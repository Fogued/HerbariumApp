package com.fogued.herbariumapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Find the ImageView by its ID
        val splashImageView: ImageView = findViewById(R.id.splashImageView)

        // Load the animation
        val popUpAnimation = AnimationUtils.loadAnimation(this, R.anim.pop_up)
        splashImageView.startAnimation(popUpAnimation)

        // Delay for 3 seconds and then start MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@SplashActivity, Login::class.java))
            finish()
        }, 3000) // 2000 milliseconds = 2 seconds
    }
}