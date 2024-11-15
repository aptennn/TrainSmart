package com.example.trainsmart

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // NIGHT MODE ALWAYS OFF
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)

            val signInButton: Button = findViewById(R.id.SignInButton)
            val login = findViewById<EditText>(R.id.SignUpLogin)
            val email = findViewById<EditText>(R.id.SignUpEmail)
            val password = findViewById<EditText>(R.id.SignUpPassword)
            val confirmPassword = findViewById<EditText>(R.id.SignUpPasswordConfirm)

            auth = Firebase.auth

            signInButton.setOnClickListener {
                val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                if (checkAllFilled(login, email, password, confirmPassword))
                {
                    // TODO: firebase auth
                    try {
                        auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                        startActivity(intent)
                    }
                    catch (e: Exception){
                        Log.e("FIREBASE ERROR", e.toString())}
                }

               // startActivity(intent)
            }

            insets
        }

    }

    private fun checkAllFilled(login: EditText, email: EditText, password: EditText, confirmPassword: EditText): Boolean {
        return !(checkETisEmpty(login) || checkETisEmpty(email) || checkETisEmpty(password) || checkETisEmpty(confirmPassword))
    }

    private fun checkETisEmpty(editText: EditText): Boolean { return editText.text.toString() == "" }

}