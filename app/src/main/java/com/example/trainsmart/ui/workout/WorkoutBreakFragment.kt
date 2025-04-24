package com.example.trainsmart.ui.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import com.example.trainsmart.R
import com.example.trainsmart.WorkoutActivity
import com.example.trainsmart.ui.exercises.ExerciseListItemModel
import com.example.trainsmart.ui.workout.view.WorkoutProgressBar
import com.example.trainsmart.ui.workouts.Workout
import java.util.Timer
import java.util.TimerTask

private const val ARGNAME_WORKOUT = "workout"
private const val ARGNAME_NEXT_EXERCISE_INDEX = "nextExerciseIndex"
private const val ARGNAME_NEXT_SET_INDEX = "nextSetIndex"
private const val BREAK_TIME: Int = 120

class WorkoutBreakFragment : Fragment() {
    private var workout: Workout? = null
    private var nextExerciseIndex: Int? = null
    private var nextSetIndex: Int? = null
    private var nextExercise: ExerciseListItemModel? = null
    private var title: TextView? = null
    private var image: ImageView? = null
    private var breakTimerView: TextView? = null
    private var breakTimer: Timer = Timer()
    private var breakSecondsRemaining: Int = BREAK_TIME
    private var buttonSkip: AppCompatImageButton? = null
    private var progressBar: WorkoutProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            workout = it.getParcelable(ARGNAME_WORKOUT)
            nextExerciseIndex = it.getInt(ARGNAME_NEXT_EXERCISE_INDEX)
            nextSetIndex = it.getInt(ARGNAME_NEXT_SET_INDEX)
        }

        if (nextExerciseIndex != -1)
            nextExercise = workout?.exercises?.get(nextExerciseIndex!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_workout_break, container, false)
        val nameTextView: TextView = root.findViewById(R.id.nextExerciseName)
        val imageView: ImageView = root.findViewById(R.id.nextExercisePicture)

        if (nextExercise != null) {
            val nextExercise = nextExercise!!
            nameTextView.text = nextExercise.name
            imageView.setImageResource(nextExercise.photo)
        } else {
            root.findViewById<View>(R.id.labelNextExerciseName).visibility = View.GONE
        }

        this.title = nameTextView
        this.image = imageView
        this.breakTimerView = root.findViewById(R.id.breakTimer)

        buttonSkip = root.findViewById(R.id.buttonSkip)
        buttonSkip!!.setOnClickListener {
            finishBreak()
        }

        val progressBar: WorkoutProgressBar = root.findViewById(R.id.workoutProgress)
        progressBar.currentExercise = nextExerciseIndex!!
        progressBar.currentSet = nextSetIndex!!
        progressBar.setCounts = workout!!.exercises.map { parseNumSets(it.countReps) }.toTypedArray()
        this.progressBar = progressBar

        startBreakTimer()

        return root
    }

    override fun onDetach() {
        super.onDetach()
        breakTimer.cancel()
    }

    private fun finishBreak() {
        breakTimer.cancel()
        (activity as WorkoutActivity).goToNextSet()
    }

    private fun startBreakTimer() {
        val task = object : TimerTask() {
            override fun run() {
                if (breakSecondsRemaining > 0) {
                    activity!!.runOnUiThread {
                        breakTimerView!!.text = String.format("%d:%02d", breakSecondsRemaining / 60, breakSecondsRemaining % 60)
                    }
                    breakSecondsRemaining--
                } else {
                    breakTimer.cancel()
                    finishBreak()
                }
            }
        }
        breakTimer.schedule(task, 0, 1000)
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
            WorkoutBreakFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARGNAME_WORKOUT, workout)
                    putInt(ARGNAME_NEXT_EXERCISE_INDEX, exerciseIndex)
                    putInt(ARGNAME_NEXT_SET_INDEX, setIndex)
                }
            }
    }
}