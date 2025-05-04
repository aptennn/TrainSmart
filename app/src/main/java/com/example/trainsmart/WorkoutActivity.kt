package com.example.trainsmart

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.trainsmart.R.id
import com.example.trainsmart.R.layout
import com.example.trainsmart.firestore.FireStoreClient
import com.example.trainsmart.ui.workout.WorkoutBreakFragment
import com.example.trainsmart.ui.workout.WorkoutExerciseFragment
import com.example.trainsmart.ui.workouts.Workout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WorkoutActivity : AppCompatActivity() {
    private var workout: Workout? = null
    private var currentExerciseIndex: Int = -1
    private var currentSetIndex: Int = -1

    private var workoutStartTime: Long = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(layout.activity_workout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        workoutStartTime = System.currentTimeMillis()

        workout = intent.extras!!.getParcelable("workoutKey")!!
    }

    fun onCountdownEnd() {
        currentExerciseIndex = 0
        currentSetIndex = 0
        val nextFragment =
            WorkoutExerciseFragment.newInstance(workout!!, currentExerciseIndex, currentSetIndex)
        supportFragmentManager.beginTransaction()
            .replace(id.workoutFragment, nextFragment)
            .commit()
    }

    fun startSetBreak() {
        val lastSet = workout!!.exercises[currentExerciseIndex].countSets.toInt() - 1
        if (currentSetIndex == lastSet) {
            if (currentExerciseIndex == workout!!.exercises.size - 1) {
                currentExerciseIndex = -1
                currentSetIndex = -1
            } else {
                currentExerciseIndex++
                currentSetIndex = 0
            }
        } else {
            currentSetIndex++
        }
        val nextFragment =
            WorkoutBreakFragment.newInstance(workout!!, currentExerciseIndex, currentSetIndex)
        supportFragmentManager.beginTransaction()
            .replace(R.id.workoutFragment, nextFragment)
            .commit()
    }

    fun goToNextSet() {
        if (currentExerciseIndex == -1) {
            val workoutEndTime = System.currentTimeMillis()
            val durationMillis = workoutEndTime - workoutStartTime
            val completeTime = formatDuration(durationMillis)

            val userId = getCurrentUserId() // нужно реализовать
            val date = getCurrentDate()     // нужно реализовать
            val workoutId = workout?.id ?: return

            lifecycleScope.launch {
                FireStoreClient().addWorkoutCompleteTime(
                    userId, date, workoutId, completeTime
                ).collect { success ->
                    // можно отобразить Snackbar или лог
                }
            }

            finish()
            return
        }

        val nextFragment = WorkoutExerciseFragment.newInstance(workout!!, currentExerciseIndex, currentSetIndex)
        supportFragmentManager.beginTransaction()
            .replace(id.workoutFragment, nextFragment)
            .commit()
    }

    private fun formatDuration(durationMillis: Long): String {
        val seconds = (durationMillis / 1000) % 60
        val minutes = (durationMillis / (1000 * 60)) % 60
        val hours = durationMillis / (1000 * 60 * 60)

        return if (hours > 0)
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        else
            String.format("%02d:%02d", minutes, seconds)
    }

    private fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date())
    }

    private fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: "unknown_user"
    }

}
