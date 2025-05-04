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

        val tvEmail: TextView = view.findViewById(R.id.UserEmailSettings)
        val tvIsVerified: TextView = view.findViewById(R.id.TvIsVerified)
        val btnLogOut: Button = view.findViewById(R.id.BtnLogOut)
        val btnVerify: Button = view.findViewById(R.id.Verify)
        val btnCheckVerification: Button = view.findViewById(R.id.BtnCheckVerification)

        tvEmail.text = getCachedEmail()

        updateVerificationStatus(tvIsVerified, btnVerify)

        btnVerify.setOnClickListener {
            auth.currentUser?.sendEmailVerification()
                ?.addOnCompleteListener {
                    AlertDialog.Builder(requireContext())
                        .setMessage("Письмо отправлено! После подтверждения нажмите 'Я подтвердил'")
                        .setPositiveButton("OK", null)
                        .show()

                    btnCheckVerification.visibility = View.VISIBLE
                }
                ?.addOnFailureListener {
                    AlertDialog.Builder(requireContext())
                        .setMessage("Ошибка отправки письма: ${it.localizedMessage}")
                        .setPositiveButton("OK", null)
                        .show()
                }
        }

        btnCheckVerification.setOnClickListener {
            auth.currentUser?.reload()?.addOnCompleteListener { reloadTask ->
                if (reloadTask.isSuccessful) {
                    if (auth.currentUser?.isEmailVerified == true) {
                        tvIsVerified.text = "Подтвержден"
                        btnVerify.visibility = View.GONE
                        btnCheckVerification.visibility = View.GONE

                        AlertDialog.Builder(requireContext())
                            .setMessage("Email подтвержден!")
                            .setPositiveButton("OK", null)
                            .show()
                    } else {
                        AlertDialog.Builder(requireContext())
                            .setMessage("Почта все еще не подтверждена.")
                            .setPositiveButton("OK", null)
                            .show()
                    }
                } else {
                    AlertDialog.Builder(requireContext())
                        .setMessage("Не удалось обновить статус пользователя.")
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
        }


        btnLogOut.setOnClickListener {
            auth.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun updateVerificationStatus(tv: TextView, btnVerify: Button) {
        val user = auth.currentUser
        if (user != null && user.isEmailVerified) {
            tv.text = "Подтвержден"
            btnVerify.visibility = View.GONE
        } else {
            tv.text = "Не подтвержден"
            btnVerify.visibility = View.VISIBLE
        }
    }


    private fun getCachedEmail(): String? {
        val sharedPref = this.activity?.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        return sharedPref?.getString("email",null)
    }
}