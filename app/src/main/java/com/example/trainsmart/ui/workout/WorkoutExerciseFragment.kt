package com.example.trainsmart.ui.workout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import com.example.trainsmart.R
import com.example.trainsmart.WorkoutActivity
import com.example.trainsmart.ui.exercises.ExerciseListItemModel
import com.example.trainsmart.ui.workout.view.WorkoutProgressBar
import com.example.trainsmart.ui.workouts.Workout
import java.util.Timer
import java.util.TimerTask

private const val ARGNAME_WORKOUT = "workout"
private const val ARGNAME_EXERCISE_INDEX = "exerciseIndex"
private const val ARGNAME_SET_INDEX = "setIndex"
private const val BREAK_TIME: Int = 120

class WorkoutExerciseFragment : Fragment() {
    private var workout: Workout? = null
    private var exerciseIndex: Int? = null
    private var setIndex: Int? = null
    private var exercise: ExerciseListItemModel? = null
    private var nSets: Int? = null
    private var state: ExerciseSetState = ExerciseSetState.NotStarted
    private var title: TextView? = null
    private var repetitionCountView: TextView? = null
    private var repetitionCountLabel: TextView? = null
    private var image: ImageView? = null
    private var breakTimerView: TextView? = null
    private var breakTimer: Timer = Timer()
    private var breakSecondsRemaining: Int = BREAK_TIME
    private var buttonStartStop: AppCompatImageButton? = null
    private var progressBar: WorkoutProgressBar? = null

    enum class ExerciseSetState {
        NotStarted, Started, Finished
    }

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
        this.title = nameTextView
        val imageView: ImageView = root.findViewById(R.id.currentExercisePicture)
        imageView.setImageResource(exercise.photo)
        this.image = imageView
        this.breakTimerView = root.findViewById(R.id.breakTimer)
        repetitionCountView = root.findViewById(R.id.currentExerciseRepetitionCount)
        repetitionCountView!!.text = parseNumReps(exercise.countReps).toString()
        repetitionCountLabel = root.findViewById(R.id.labelCurrentExerciseRepetitionCount)

        buttonStartStop = root.findViewById(R.id.buttonStartStop)
        buttonStartStop!!.setOnClickListener {
            when (state) {
                ExerciseSetState.NotStarted -> startExercise()
                ExerciseSetState.Started -> finishExercise()
                ExerciseSetState.Finished -> nextExercise()
            }
        }

        val progressBar: WorkoutProgressBar = root.findViewById(R.id.workoutProgress)
        progressBar.currentExercise = exerciseIndex!!
        progressBar.currentSet = setIndex!!
        progressBar.setCounts = workout!!.exercises.map { parseNumSets(it.countReps) }.toTypedArray()
        this.progressBar = progressBar
        return root
    }

    private fun startExercise() {
        buttonStartStop!!.setImageResource(R.drawable.ic_stop_exercise)
        state = ExerciseSetState.Started
        progressBar!!.partialProgress = 0.5f
        progressBar!!.invalidate()
    }

    private fun finishExercise() {
        if (exerciseIndex != workout!!.exercises.size - 1 || setIndex != nSets!! - 1) {
            buttonStartStop!!.setImageResource(R.drawable.ic_skip_break)
            state = ExerciseSetState.Finished
            image!!.visibility = View.GONE
            startBreakTimer()
            breakTimerView!!.visibility = View.VISIBLE
            progressBar!!.partialProgress = 1.0f
            progressBar!!.invalidate()
            repetitionCountView!!.visibility = View.GONE
            repetitionCountLabel!!.visibility = View.GONE
            title!!.text = getString(R.string.exercise_break)
        } else {
            nextExercise()
        }
    }

    private fun nextExercise() {
        breakTimer.cancel()
        (activity as WorkoutActivity).goToNextSet()
    }

    private fun startBreakTimer() {
        val task = object : TimerTask() {
            override fun run() {
                if (breakSecondsRemaining > 0) {
                    breakTimerView!!.text = String.format("%d", breakSecondsRemaining)
                    breakSecondsRemaining--
                } else {
                    breakTimer.cancel()
                    nextExercise()
                }
            }
        }
        breakTimer!!.schedule(task, 0, 1000)
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