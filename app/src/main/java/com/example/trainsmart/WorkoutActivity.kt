package com.example.trainsmart

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trainsmart.ui.workout.WorkoutExerciseFragment
import com.example.trainsmart.ui.workouts.Workout

class WorkoutActivity : AppCompatActivity() {
    private var workout: Workout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_workout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        workout = intent.extras!!.getParcelable("workoutKey")!!
    }
    fun onCountdownEnd() {
        val nextFragment = WorkoutExerciseFragment.newInstance(workout!!)
        supportFragmentManager.beginTransaction()
            .replace(R.id.workoutFragment, nextFragment)
            .commit()
    }
}