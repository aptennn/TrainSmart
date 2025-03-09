package com.example.trainsmart.ui.workout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.trainsmart.R
import com.example.trainsmart.ui.exercises.ExerciseListItemModel
import com.example.trainsmart.ui.workouts.Workout

private const val ARGNAME_WORKOUT = "workout"
private const val ARGNAME_EXERCISE_INDEX = "exerciseIndex"

class WorkoutExerciseFragment : Fragment() {
    private var workout: Workout? = null
    private var exerciseIndex: Int? = null
    private var exercise: ExerciseListItemModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            workout = it.getParcelable(ARGNAME_WORKOUT)
            exerciseIndex = it.getInt(ARGNAME_EXERCISE_INDEX)
        }
        exercise = workout?.exercises?.get(exerciseIndex!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val exercise = exercise!!
        val root = inflater.inflate(R.layout.fragment_workout_exercise, container, false)
        val nameTextView: TextView = root.findViewById(R.id.currentExerciseName)
        nameTextView.text = exercise.name
        val imageView: ImageView = root.findViewById(R.id.currentExercisePicture)
        imageView.setImageResource(exercise.photo)
        val repetitionCountTextView: TextView = root.findViewById(R.id.currentExerciseRepetitionCount)
        val repetitionStringWords = exercise.countReps.split(' ')
        if (repetitionStringWords.size == 5)
            repetitionCountTextView.text = repetitionStringWords[3]
        else
            repetitionCountTextView.text = "???"

        val buttonStart: Button = root.findViewById(R.id.buttonStart)
        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(workout: Workout, exerciseIndex: Int) =
            WorkoutExerciseFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARGNAME_WORKOUT, workout)
                    putInt(ARGNAME_EXERCISE_INDEX, exerciseIndex)
                }
            }
    }
}