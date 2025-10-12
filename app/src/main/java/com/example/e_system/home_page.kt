package com.example.e_system

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class home_page : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)

//        val bottomNav = findViewById<BottomNavigationView>(R.id.buttomnavigationbar)
//
//        // Load default fragment
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.homeframe, home())
//            .commit()
//
//        bottomNav.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.home -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.homeframe, home_page())
//                        .commit()
//                    true
//                }
//                R.id.exercise -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.container, SearchFragment())
//                        .commit()
//                    true
//                }
//                R.id.attendance -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.container, ProfileFragment())
//                        .commit()
//                    true
//                }
//                else -> false
//            }
//        }
    }
}
