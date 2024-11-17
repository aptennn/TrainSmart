package com.example.trainsmart

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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

            val signUpButton: Button = findViewById(R.id.SignUpButton)
            val login = findViewById<EditText>(R.id.SignUpLogin)
            val email = findViewById<EditText>(R.id.SignUpEmail)
            val password = findViewById<EditText>(R.id.SignUpPassword)
            val confirmPassword = findViewById<EditText>(R.id.SignUpPasswordConfirm)

            auth = Firebase.auth

            signUpButton.setOnClickListener {
                val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                if (checkAllFilled(login, email, password, confirmPassword) &&
                    (password.text.toString() == confirmPassword.text.toString())
                ) {
                    createUserEmailPassword(email.text.toString(), password.text.toString())
                }
            }

            insets
        }

    }

    private fun createUserEmailPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Send email verification

                    auth.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener {
                            Toast.makeText(
                                baseContext, "Please check your e-mail to verify account",
                                Toast.LENGTH_LONG
                            ).show()
                            if (task.isSuccessful) {
                                val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                                intent.putExtra("emailCache", email)
                                Firebase.auth.signOut()
                                startActivity(intent)
                            }
                        }?.addOnFailureListener {
                            Toast.makeText(
                                baseContext, it.localizedMessage, Toast.LENGTH_LONG
                            ).show()
                        }
                }
            }.addOnFailureListener {
                Toast.makeText(
                    baseContext, it.localizedMessage, Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun checkAllFilled(
        login: EditText,
        email: EditText,
        password: EditText,
        confirmPassword: EditText
    ): Boolean {
        return !(checkETisEmpty(login) || checkETisEmpty(email) || checkETisEmpty(password) || checkETisEmpty(
            confirmPassword
        ))
    }

    private fun checkETisEmpty(editText: EditText): Boolean {
        return editText.text.toString() == ""
    }

}