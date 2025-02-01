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
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_workouts,
                R.id.navigation_exercises,
                R.id.navigation_statistics,
                R.id.navigation_settings
            )
        )

        val mainDestinationChangedListener = NavController.OnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id != 2131362176 && destination.id != 2131362175 &&destination.id != 2131362173 && destination.id != 2131362179) {
                navView.visibility = View.GONE
            } else {
                navView.visibility = View.VISIBLE
            }
        }

        navController.addOnDestinationChangedListener(mainDestinationChangedListener)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        //supportActionBar?.hide()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}