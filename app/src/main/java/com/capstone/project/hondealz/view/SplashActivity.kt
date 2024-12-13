package com.capstone.project.hondealz.view

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
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
import java.util.Locale

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var appImage: ImageView
    private lateinit var outerCircle: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLocale("in")
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

    @Suppress("DEPRECATION")
    private fun setAppLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            createConfigurationContext(config)
        } else {
            config.locale = locale
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }
}