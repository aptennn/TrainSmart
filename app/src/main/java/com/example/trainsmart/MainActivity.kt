package com.example.trainsmart

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.trainsmart.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_workouts,
                R.id.navigation_exercises,
                R.id.navigation_statistics,
                R.id.navigation_settings
            )
        )

        // Настройка BottomNavigationView
        binding.navView.setupWithNavController(navController)

        // Управление видимостью BottomBar
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_workouts,
                R.id.navigation_exercises,
                R.id.navigation_statistics,
                R.id.navigation_settings -> showBottomNav()
                else -> hideBottomNav()
            }
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
         supportActionBar?.title = "Тренировки"
        //supportActionBar?.hide()
    }



    private fun showBottomNav() {
        binding.navView.visibility = View.VISIBLE
    }

    private fun hideBottomNav() {
        binding.navView.visibility = View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}