package com.capstone.project.hondealz.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.capstone.project.hondealz.R
import com.capstone.project.hondealz.data.pref.UserPreference
import com.capstone.project.hondealz.data.pref.dataStore
import com.capstone.project.hondealz.view.fragments.HistoryFragment
import com.capstone.project.hondealz.view.fragments.HomeFragment
import com.capstone.project.hondealz.view.fragments.ProfileFragment
import com.capstone.project.hondealz.view.fragments.ScanFragment
import com.capstone.project.hondealz.view.login.LoginActivity
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var userPreference: UserPreference
    private lateinit var bottomNavigation: CurvedBottomNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userPreference = UserPreference.getInstance(dataStore)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        checkSessionAndInitializeApp()
    }

    private fun checkSessionAndInitializeApp() {
        lifecycleScope.launch {
            val userModel = userPreference.getSession().first()
            if (userModel.isLogin) {
                setupBottomNavigation()
                replaceFragment(HomeFragment())
                bottomNavigation.show(1)
            } else {
                navigateToLogin()
            }
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation.add(
            CurvedBottomNavigation.Model(1, "Home", R.drawable.ic_home_24)
        )
        bottomNavigation.add(
            CurvedBottomNavigation.Model(2, "Scan", R.drawable.ic_scan_24)
        )
        bottomNavigation.add(
            CurvedBottomNavigation.Model(3, "History", R.drawable.ic_history_24)
        )
        bottomNavigation.add(
            CurvedBottomNavigation.Model(4, "Profile", R.drawable.ic_profile_24)
        )

        bottomNavigation.setOnClickMenuListener {
            when (it.id) {
                1 -> replaceFragment(HomeFragment())
                2 -> replaceFragment(ScanFragment())
                3 -> replaceFragment(HistoryFragment())
                4 -> replaceFragment(ProfileFragment())
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}