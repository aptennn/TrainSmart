package com.example.trainsmart

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

            auth = Firebase.auth

            val cachedEmail = getCachedEmail()

            if (cachedEmail == null) {
                cardViewNull.visibility = View.VISIBLE
                llNotNull.visibility = View.GONE

                signInButton.setOnClickListener {

                    val pass = password.text.toString()

                    signInUser(emailNull.text.toString(), pass)

                    cacheEmail(emailNull.text.toString())

                }
            }
            else {
                email.text = cachedEmail

                signInButton.setOnClickListener {

                    val pass = password.text.toString()

                    signInUser(email.text.toString(), pass)

                }
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
                    val user = auth.currentUser
                    val intent = Intent(this@SignInActivity, MainActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Authentication failed.")
                    builder.setPositiveButton("OK") { dialog, which ->
                        dialog.dismiss()
                    }
                    val dialog = builder.create()

                    dialog.show()
                }
            }
    }

    private fun getCachedEmail(): String? {
        val sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        return sharedPref.getString("email",null)
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