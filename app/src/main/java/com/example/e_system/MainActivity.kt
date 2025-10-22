package com.example.e_system

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.e_system.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Home())
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    replaceFragment(Home())
                    true
                }
                R.id.exercise -> {
                    replaceFragment(Exercise())
                    true
                }
                R.id.attendance -> {
                    replaceFragment(Attendance())
                    true
                }
                else -> false
            }
        }
        }
        private fun replaceFragment(fragment: Fragment) {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_layout, fragment)
            fragmentTransaction.commit()
        }
    }
