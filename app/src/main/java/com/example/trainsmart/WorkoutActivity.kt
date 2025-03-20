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
    private var currentExerciseIndex: Int = -1
    private var currentSetIndex: Int = -1

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
        currentExerciseIndex = 0
        currentSetIndex = 0
        val nextFragment = WorkoutExerciseFragment.newInstance(workout!!, currentExerciseIndex, currentSetIndex)
        supportFragmentManager.beginTransaction()
            .replace(R.id.workoutFragment, nextFragment)
            .commit()
    }
    fun onNextSetClicked() {
        if (currentSetIndex == parseNumSets(workout!!.exercises[currentExerciseIndex].countReps) - 1) {
            if (currentExerciseIndex == workout!!.exercises.size - 1) {
                finish()
                return
            } else {
                currentExerciseIndex++
                currentSetIndex = 0
            }
        } else {
            currentSetIndex++
        }
        val nextFragment = WorkoutExerciseFragment.newInstance(workout!!, currentExerciseIndex, currentSetIndex)
        supportFragmentManager.beginTransaction()
            .replace(R.id.workoutFragment, nextFragment)
            .commit()
    }

    private fun parseNumReps(s: String): Int {
        val words = s.split(' ')
        if (words.size != 5)
            throw IllegalArgumentException("invalid sets+reps string")
        return words[3].toInt()
    }

    private fun parseNumSets(s: String): Int {
        val words = s.split(' ')
        if (words.size != 5)
            throw IllegalArgumentException("invalid sets+reps string")
        return words[0].toInt()
    }
}