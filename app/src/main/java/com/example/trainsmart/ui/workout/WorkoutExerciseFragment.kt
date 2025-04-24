package com.example.trainsmart.ui.workout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import coil.load
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
private const val SET_TIME = 60

class WorkoutExerciseFragment : Fragment() {
    private var workout: Workout? = null
    private var exerciseIndex: Int? = null
    private var setIndex: Int? = null
    private var exercise: ExerciseListItemModel? = null
    private var nSets: Int? = null
    private var title: TextView? = null
    private var repetitionCountView: TextView? = null
    private var repetitionCountLabel: TextView? = null
    private var image: ImageView? = null
    private var setTimerView: TextView? = null
    private var setTimer: Timer = Timer()
    private var setSecondsRemaining: Int = SET_TIME
    private var buttonStartStop: AppCompatImageButton? = null
    private var progressBar: WorkoutProgressBar? = null
    private var setStarted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            workout = it.getParcelable(ARGNAME_WORKOUT)
            exerciseIndex = it.getInt(ARGNAME_EXERCISE_INDEX)
            setIndex = it.getInt(ARGNAME_SET_INDEX)
        }

        exercise = workout?.exercises?.get(exerciseIndex!!)
        nSets = exercise?.countSets!!.toInt()
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
        imageView.load(exercise.photo)
        this.image = imageView
        this.setTimerView = root.findViewById(R.id.setTimer)
        repetitionCountView = root.findViewById(R.id.currentExerciseRepetitionCount)
        repetitionCountView!!.text = exercise.countReps
        repetitionCountLabel = root.findViewById(R.id.labelCurrentExerciseRepetitionCount)

        buttonStartStop = root.findViewById(R.id.buttonStartStop)
        buttonStartStop!!.setOnClickListener {
            if (!setStarted) {
                startSet()
            } else {
                finishSet()
            }
        }
        setTimerView!!.text =
            String.format("%d:%02d", setSecondsRemaining / 60, setSecondsRemaining % 60)

        val progressBar: WorkoutProgressBar = root.findViewById(R.id.workoutProgress)
        progressBar.currentExercise = exerciseIndex!!
        progressBar.currentSet = setIndex!!
        progressBar.setCounts = workout!!.exercises.map { it.countSets.toInt() }.toTypedArray()
        this.progressBar = progressBar

        return root
    }

    override fun onDetach() {
        super.onDetach()
        setTimer.cancel()
    }

    private fun startSet() {
        buttonStartStop!!.setImageResource(R.drawable.ic_stop_exercise)
        setStarted = true
        startTimer()
    }

    private fun finishSet() {
        setTimer.cancel()
        (activity as WorkoutActivity).startSetBreak()
    }

    private fun startTimer() {
        setTimer.cancel()
        setTimer = Timer()
        setSecondsRemaining = SET_TIME

        val task = object : TimerTask() {
            override fun run() {
                if (setSecondsRemaining > 0) {
                    activity?.runOnUiThread {
                        setTimerView!!.text =
                            String.format(
                                "%d:%02d",
                                setSecondsRemaining / 60,
                                setSecondsRemaining % 60
                            )
                        progressBar?.partialProgress =
                            1f - setSecondsRemaining.toFloat() / SET_TIME
                        progressBar?.invalidate()
                    }
                    setSecondsRemaining--
                } else {
                    setTimer.cancel()
                    activity?.runOnUiThread { finishSet() }
                }
            }
        }
        setTimer.schedule(task, 0, 1000)
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