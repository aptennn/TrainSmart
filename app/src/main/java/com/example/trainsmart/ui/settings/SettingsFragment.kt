package com.example.trainsmart.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.trainsmart.LoginActivity
import com.example.trainsmart.R
import com.example.trainsmart.SignInActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SettingsFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // TODO: Use the ViewModel

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Настройки"
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    /** WARNING!
     * THE CODE BELOW IS JUST FOR TEST PURPOSES.
     * DO NOT WORK WITH FRAGMENTS LIKE THAT ! */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Настройки"
        val cachedEmail = getCachedEmail()

        val v: View? = getView()
        val btnLogOut: Button? = v?.findViewById(R.id.BtnLogOut)
        val btnVerify: Button? = v?.findViewById(R.id.Verify)

        val tvEmail: TextView? = v?.findViewById(R.id.UserEmailSettings)
        val tvIsVerified: TextView? = v?.findViewById(R.id.TvIsVerified)

        if (tvEmail != null) {
            tvEmail.text = getCachedEmail()
        }

        if (auth.currentUser?.isEmailVerified!!) {
            if (tvIsVerified != null) {
                tvIsVerified.text = "Verified."
            }
        }
        else
            {
                if (tvIsVerified != null) {
                    tvIsVerified.text = "Not verified."
                }
                if (btnVerify != null) {
                    btnVerify.visibility = View.VISIBLE
                }
            }

        btnLogOut?.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        btnVerify?.setOnClickListener {
            // Send email verification

            auth.currentUser?.sendEmailVerification()
                ?.addOnCompleteListener {
                    val builder = AlertDialog.Builder(requireActivity())
                    builder.setMessage("Please check your e-mail to verify account")
                    builder.setPositiveButton("OK") { dialog, which ->
                        Firebase.auth.signOut()
                        val intent = Intent(this.activity, SignInActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                    }
                    val dialog = builder.create()

                    dialog.show()

                }?.addOnFailureListener {
                    val builder = AlertDialog.Builder(requireActivity())
                    builder.setMessage(it.localizedMessage)
                    builder.setPositiveButton("OK") { dialog, which ->
                        dialog.dismiss()
                    }
                    val dialog = builder.create()

                    dialog.show()
                }
        }



    }

    private fun getCachedEmail(): String? {
        val sharedPref = this.activity?.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        return sharedPref?.getString("email",null)
    }
}