package com.example.trainsmart.ui.workout

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.trainsmart.R
import com.example.trainsmart.WorkoutActivity
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

/**
 * A simple [Fragment] subclass.
 * Use the [WorkoutCountdownFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WorkoutCountdownFragment : Fragment() {
    private var countdown: Timer = Timer()
    private var countdownSeconds: Int = 5
    private var countdownSecondsLabel: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var countdownTimerTask: TimerTask? = null
        countdownTimerTask = object : TimerTask() {
            override fun run() {
                val activity = this@WorkoutCountdownFragment.activity as WorkoutActivity
                activity.runOnUiThread {
                    this@WorkoutCountdownFragment.decreaseCountdown()
                    if (countdownSeconds == 0) {
                        countdownTimerTask?.cancel()
                        activity.onCountdownEnd()
                    }
                }
            }
        }
        countdown.schedule(countdownTimerTask, 1000, 1000)
        val root = inflater.inflate(R.layout.fragment_workout_countdown, container, false)
        countdownSecondsLabel = root.findViewById(R.id.countdownSeconds)
        // Inflate the layout for this fragment
        return root
    }

    fun decreaseCountdown() {
        countdownSecondsLabel?.text = String.format(Locale.getDefault(), "%d", --countdownSeconds)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment WorkoutCountdownFragment.
         */
        @JvmStatic
        fun newInstance() =
            WorkoutCountdownFragment()
    }
}