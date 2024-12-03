package com.capstone.project.hondealz.view.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.capstone.project.hondealz.R
import com.capstone.project.hondealz.databinding.ActivityMainBinding
import com.capstone.project.hondealz.view.ViewModelFactory
import com.capstone.project.hondealz.view.fragments.HistoryFragment
import com.capstone.project.hondealz.view.fragments.ProfileFragment
import com.capstone.project.hondealz.view.fragments.ScanFragment
import com.capstone.project.hondealz.view.fragments.home.HomeFragment
import com.capstone.project.hondealz.view.welcome.WelcomeActivity
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels { ViewModelFactory.getInstance(this) }
    private var isBottomNavigationInitialized = false
    private var lastSelectedNavItemId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkSessionAndInitializeApp()
    }

    private fun checkSessionAndInitializeApp() {
        mainViewModel.getSession().observe(this) { userModel ->
            if (userModel.isLogin) {
                if (!isBottomNavigationInitialized) {
                    setupBottomNavigation()
                    isBottomNavigationInitialized = true
                }

                val fragmentToShow = when (lastSelectedNavItemId) {
                    1 -> HomeFragment()
                    2 -> ScanFragment()
                    3 -> HistoryFragment()
                    4 -> ProfileFragment()
                    else -> HomeFragment()
                }

                replaceFragment(fragmentToShow)
                binding.bottomNavigation.show(lastSelectedNavItemId)
            } else {
                navigateToWelcome()
            }
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.add(
            CurvedBottomNavigation.Model(1, getString(R.string.home), R.drawable.ic_home_24)
        )
        binding.bottomNavigation.add(
            CurvedBottomNavigation.Model(2, getString(R.string.scan), R.drawable.ic_scan_24)
        )
        binding.bottomNavigation.add(
            CurvedBottomNavigation.Model(3, getString(R.string.history), R.drawable.ic_history_24)
        )
        binding.bottomNavigation.add(
            CurvedBottomNavigation.Model(4, getString(R.string.profile), R.drawable.ic_profile_24)
        )

        binding.bottomNavigation.setOnClickMenuListener {
            lastSelectedNavItemId = it.id
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
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun navigateToWelcome() {
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("LAST_SELECTED_NAV_ITEM", lastSelectedNavItemId)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        lastSelectedNavItemId = savedInstanceState.getInt("LAST_SELECTED_NAV_ITEM", 1)
    }
}