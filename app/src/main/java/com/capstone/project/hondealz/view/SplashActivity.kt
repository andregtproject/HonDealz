package com.capstone.project.hondealz.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.capstone.project.hondealz.R
import com.capstone.project.hondealz.view.main.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var appImage: ImageView
    private lateinit var outerCircle: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        appImage = findViewById(R.id.app_image)
        outerCircle = findViewById(R.id.outer_circle)

        val logoAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.logo_animation)
        val circleAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.circle_animation)

        outerCircle.startAnimation(circleAnimation)
        appImage.startAnimation(logoAnimation)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2500)
    }
}