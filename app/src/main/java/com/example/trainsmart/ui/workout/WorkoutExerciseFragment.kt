package com.example.trainsmart.ui.workout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import coil.load
import com.example.trainsmart.R
import com.example.trainsmart.WorkoutActivity
import com.example.trainsmart.ui.exercises.ExerciseListItemModel
import com.example.trainsmart.ui.workout.view.WorkoutProgressBar
import com.example.trainsmart.ui.workouts.Workout

private const val ARGNAME_WORKOUT = "workout"
private const val ARGNAME_EXERCISE_INDEX = "exerciseIndex"
private const val ARGNAME_SET_INDEX = "setIndex"

class WorkoutExerciseFragment : Fragment() {
    private var workout: Workout? = null
    private var exerciseIndex: Int? = null
    private var setIndex: Int? = null
    private var exercise: ExerciseListItemModel? = null
    private var nSets: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            workout = it.getParcelable(ARGNAME_WORKOUT)
            exerciseIndex = it.getInt(ARGNAME_EXERCISE_INDEX)
            setIndex = it.getInt(ARGNAME_SET_INDEX)
        }
        println("error here")

        exercise = workout?.exercises?.get(exerciseIndex!!)
        println(exercise?.countReps!!)
        nSets = parseNumSets(exercise?.countReps!!)
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
        imageView.load(exercise.photo)
        val repetitionCountTextView: TextView = root.findViewById(R.id.currentExerciseRepetitionCount)
        repetitionCountTextView.text = parseNumReps(exercise.countReps).toString()

        val buttonStart: Button = root.findViewById(R.id.buttonStart)
        buttonStart.setOnClickListener { (activity as WorkoutActivity).onNextSetClicked() }

        val progressBar: WorkoutProgressBar = root.findViewById(R.id.workoutProgress)
        progressBar.currentExercise = exerciseIndex!!
        progressBar.currentSet = setIndex!!
        progressBar.setCounts = workout!!.exercises.map { parseNumSets(it.countReps) }.toTypedArray()
        return root
    }

    private fun parseNumReps(s: String): Int {
        val words = s.split('-')
        if (words.size != 2)
            throw IllegalArgumentException("invalid sets+reps string")
        return words[1].toInt()
    }

    private fun parseNumSets(s: String): Int {
        val words = s.split('-')
        if (words.size != 2)
            throw IllegalArgumentException("invalid sets+reps string")
        return words[0].toInt()
    }

    companion object {
        @JvmStatic
        fun newInstance(workout: Workout, exerciseIndex: Int, setIndex: Int) =
            WorkoutExerciseFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARGNAME_WORKOUT, workout)
                    putInt(ARGNAME_EXERCISE_INDEX, exerciseIndex)
                    putInt(ARGNAME_SET_INDEX, setIndex)
                }
            }
    }
}