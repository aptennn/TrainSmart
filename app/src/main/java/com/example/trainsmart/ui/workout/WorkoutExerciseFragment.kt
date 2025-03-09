package com.example.trainsmart.ui.workout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.trainsmart.R
import com.example.trainsmart.ui.workouts.Workout

private const val ARGNAME_WORKOUT = "workout"

class WorkoutExerciseFragment : Fragment() {
    private var workout: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            workout = it.getString(ARGNAME_WORKOUT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_workout_exercise, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(workout: Workout) =
            WorkoutExerciseFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARGNAME_WORKOUT, workout)
                }
            }
    }
}