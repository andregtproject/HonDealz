package com.capstone.project.hondealz.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.capstone.project.hondealz.R
import com.capstone.project.hondealz.view.fragments.HistoryFragment
import com.capstone.project.hondealz.view.fragments.HomeFragment
import com.capstone.project.hondealz.view.fragments.ProfileFragment
import com.capstone.project.hondealz.view.fragments.ScanFragment
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation = findViewById<CurvedBottomNavigation>(R.id.bottomNavigation)
        bottomNavigation.add(
            CurvedBottomNavigation.Model(1,"Home", R.drawable.ic_home_24)
        )
        bottomNavigation.add(
            CurvedBottomNavigation.Model(2,"Scan", R.drawable.ic_scan_24)
        )
        bottomNavigation.add(
            CurvedBottomNavigation.Model(3,"History", R.drawable.ic_history_24)
        )
        bottomNavigation.add(
            CurvedBottomNavigation.Model(4,"Profile", R.drawable.ic_profile_24)
        )

        bottomNavigation.setOnClickMenuListener {
            when(it.id){
                1 -> {
                    replaceFragment(HomeFragment())
                }
                2 -> {
                    replaceFragment(ScanFragment())
                }
                3 -> {
                    replaceFragment(HistoryFragment())
                }
                4 -> {
                    replaceFragment(ProfileFragment())
                }
            }
        }

        // default Bottom Tab Selected
        replaceFragment(HomeFragment())
        bottomNavigation.show(1)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer,fragment)
            .commit()
    }
}