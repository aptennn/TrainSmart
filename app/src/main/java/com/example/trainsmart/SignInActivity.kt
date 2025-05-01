package com.example.trainsmart

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // NIGHT MODE ALWAYS OFF
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)

            val signInButton: Button = findViewById(R.id.SignUpButton)

            val returnButton: Button = findViewById(R.id.ReturnToLoginFromSignIn)

            val email: TextView = findViewById(R.id.UserEmail)

            val emailNull: EditText = findViewById(R.id.UserEmailNull)

            val cardViewNull: CardView = findViewById(R.id.CardViewNull)

            val password: EditText = findViewById(R.id.SignInPassword)

            val llNotNull: LinearLayout = findViewById(R.id.LLNotNull)

            val otherUserButton: Button = findViewById(R.id.UserChange)

            val forgotPassword: Button = findViewById(R.id.ForgotPassword)

            auth = Firebase.auth

            val cachedEmail = getCachedEmail()

            if (cachedEmail == null) { // checking is there any previous user stored
                cardViewNull.visibility = View.VISIBLE // setting layout
                llNotNull.visibility = View.GONE // case: user not found in db

                signInButton.setOnClickListener {

                    val pass = password.text.toString()

                    if (emailNull.text.toString() != "" && password.text.toString() != "") {

                        signInUser(emailNull.text.toString(), pass)

                        cacheEmail(emailNull.text.toString())
                    } else {
                        if (emailNull.text.toString() == "") {
                            Toast.makeText(this, "Введите адрес почты", Toast.LENGTH_SHORT).show()
                        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailNull.text).matches()) {
                            Toast.makeText(
                                this,
                                "Введите корректный адрес почты",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (password.text.toString() == "") {
                            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show()
                        }

                    }

                }

                forgotPassword.setOnClickListener {

                    val email = emailNull.text.toString()

                    if (email.isEmpty()) {
                        Toast.makeText(this, "Введите адрес почты", Toast.LENGTH_SHORT)
                            .show()
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(this, "Введите корректный адрес почты", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        sendPasswordResetEmail(email)
                    }
                }

            } else { // user found in db
                email.text = cachedEmail

                signInButton.setOnClickListener {

                    val pass = password.text.toString()

                    if (password.text.toString() != "") {

                        signInUser(email.text.toString(), pass)

                    } else {
                        Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show()
                    }

                }

                forgotPassword.setOnClickListener {
                    sendPasswordResetEmail(cachedEmail)
                }

                // change layout and behaviour to user not found in db case
                otherUserButton.setOnClickListener {

                    clearCachedEmail()

                    cardViewNull.visibility = View.VISIBLE
                    llNotNull.visibility = View.GONE


                    signInButton.setOnClickListener {

                        val pass = password.text.toString()

                        if (emailNull.text.toString() != "" && password.text.toString() != "") {

                            signInUser(emailNull.text.toString(), pass)

                            cacheEmail(emailNull.text.toString())
                        } else {
                            if (emailNull.text.toString() == "") {
                                Toast.makeText(this, "Введите адрес почты", Toast.LENGTH_SHORT)
                                    .show()
                            } else if (password.text.toString() == "") {
                                Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show()
                            }

                        }

                    }

                }
                // end other user

            }

            returnButton.setOnClickListener {
                val intent = Intent(this@SignInActivity, LoginActivity::class.java)
                startActivity(intent)
            }

            insets
        }

    }

    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    auth.currentUser

//                    if (user?.isEmailVerified!!) {
                    val intent = Intent(this@SignInActivity, MainActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
//                    }
//                    else
//                    {
//                        Toast.makeText(this, "Please, verify your account before sign in", Toast.LENGTH_SHORT).show()
//                    }


                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Ошибка аутентификации.")
                    builder.setPositiveButton("ОК") { dialog, which ->
                        dialog.dismiss()
                    }
                    val dialog = builder.create()

                    dialog.show()
                }
            }
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Письмо для сброса пароля отправлено на $email",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Ошибка: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun getCachedEmail(): String? {
        val sharedPref = getSharedPreferences("myPref", MODE_PRIVATE)
        return sharedPref.getString("email", null)
    }

    private fun clearCachedEmail() {
        val sharedPref = getSharedPreferences("myPref", MODE_PRIVATE)
        sharedPref.edit { remove("email") }
    }

    @SuppressLint("CommitPrefEdits")
    private fun cacheEmail(email: String) {
        val sharedPref = getSharedPreferences("myPref", MODE_PRIVATE)
        val editor = sharedPref.edit()

        editor.apply {
            putString("email", email)

            apply()
        }

    }

}