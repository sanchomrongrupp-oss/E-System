package com.example.e_system
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class introl_three : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_introl_three)

        val btnnexttosigin = findViewById<LinearLayout?>(R.id.btnnextintrol3)
        btnnexttosigin?.setOnClickListener(View.OnClickListener { v: View? ->
            val intent = Intent(this@introl_three, signin::class.java)
            startActivity(intent)
        })

    }
}