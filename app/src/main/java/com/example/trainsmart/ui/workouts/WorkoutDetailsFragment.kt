package com.example.trainsmart.ui.workouts

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trainsmart.R
import com.example.trainsmart.WorkoutActivity

class WorkoutsDetailsFragment : Fragment() {

    private var workout: Workout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            workout = it.getParcelable("workoutKey")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_workout_details, container, false)

        val workoutTitle: TextView = view.findViewById(R.id.tv_exercise_title)
        val workoutTime: TextView = view.findViewById(R.id.tv_exercise_time)
        val workoutCountExersices: TextView = view.findViewById(R.id.tv_exercise_count_exercises)
        val workoutImage: ImageView = view.findViewById(R.id.iv_exercise)
        val rV: RecyclerView = view.findViewById(R.id.rv_exercises)
        val startButton: Button = view.findViewById(R.id.btn_start)

        workout?.let{
            workoutTitle.text = it.title
            workoutTime.text = "${it.time} часа"
            workoutCountExersices.text = "${it.exercises.size} упражнения"
            workoutImage.setImageResource(it.photo)
            rV.layoutManager = LinearLayoutManager(requireContext())
            rV.adapter = ExerciseAdapter(it.exercises)
        }

        startButton.setOnClickListener {
            startActivity(Intent(context, WorkoutActivity::class.java))
        }

        return view
    }
}