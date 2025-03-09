package com.example.trainsmart

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
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
            val returnButton: Button = findViewById(R.id.ReturnToLoginFromSignUp)
            val login = findViewById<EditText>(R.id.SignUpLogin)
            val email = findViewById<EditText>(R.id.SignUpEmail)
            val password = findViewById<EditText>(R.id.SignUpPassword)
            val confirmPassword = findViewById<EditText>(R.id.SignUpPasswordConfirm)

            auth = Firebase.auth

            signUpButton.setOnClickListener {
                if (checkAllFilled(login, email, password, confirmPassword) &&
                    (password.text.toString() == confirmPassword.text.toString())
                ) {
                    createUserEmailPassword(email.text.toString(), password.text.toString())
                } else if (password.text.toString() != confirmPassword.text.toString()) {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("The confirmation password does not match the original one")
                    builder.setPositiveButton("OK") { dialog, which ->
                        dialog.dismiss()
                    }
                    val dialog = builder.create()

                    dialog.show()
                }
            }

            returnButton.setOnClickListener {
                val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                startActivity(intent)
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
                            val builder = AlertDialog.Builder(this)
                            builder.setMessage("Please check your e-mail to verify account")
                            builder.setPositiveButton("OK") { dialog, which ->
                                cacheEmail(email)
                                Firebase.auth.signOut()
                                dialog.dismiss()
                                val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                                startActivity(intent)
                            }
                            val dialog = builder.create()

                            dialog.show()

//                            if (task.isSuccessful) {
//                                val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
//                                cacheEmail(email)
//                                Firebase.auth.signOut()
//                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                                startActivity(intent)
//                            }
                        }?.addOnFailureListener {
                            val builder = AlertDialog.Builder(this)
                            builder.setMessage(it.localizedMessage)
                            builder.setPositiveButton("OK") { dialog, which ->
                                dialog.dismiss()
                            }
                            val dialog = builder.create()

                            dialog.show()
                        }
                }
            }.addOnFailureListener {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(it.localizedMessage)
                builder.setPositiveButton("OK") { dialog, which ->
                    dialog.dismiss()
                }
                val dialog = builder.create()

                dialog.show()
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

    @SuppressLint("CommitPrefEdits")
    private fun cacheEmail(email: String) {
        val sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        editor.apply {
            putString("email", email)

            apply()
        }

    }

}