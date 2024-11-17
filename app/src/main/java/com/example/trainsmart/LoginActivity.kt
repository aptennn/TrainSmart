package com.example.trainsmart

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // NIGHT MODE ALWAYS OFF
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)

            val toSignInScreenButton: Button = findViewById(R.id.ToSignInScreenButton)
            val toSignUpScreenButton: Button = findViewById(R.id.ToSignUpScreenButton)

            toSignInScreenButton.setOnClickListener {
                val intent = Intent(this@LoginActivity, SignInActivity::class.java)
                startActivity(intent)
            }

            toSignUpScreenButton.setOnClickListener {
                val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
                startActivity(intent)
            }

            auth = Firebase.auth

            if (auth.currentUser != null) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
            }

            insets
        }
    }

}