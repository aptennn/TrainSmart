package com.example.rainsmart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // NIGHT MODE ALWAYS OFF
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)

            val SIScreenSignIN: Button = findViewById(R.id.SIScreenSignIn)

            SIScreenSignIN.setOnClickListener {
                val intent = Intent(this@SignInActivity, MainActivity::class.java)
                startActivity(intent)
            }

            insets
        }
    }
}