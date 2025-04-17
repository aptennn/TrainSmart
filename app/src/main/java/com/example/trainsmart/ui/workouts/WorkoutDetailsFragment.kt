package com.example.trainsmart.ui.workouts

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
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
        // ПОМЕНЯЛ с savedInstanceState на null чтоб без наложения
        super.onCreate(null)
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

        val workoutTitle: TextView = view.findViewById(R.id.tvTitle)
        val workoutTime: TextView = view.findViewById(R.id.timeTextView)
        val workoutCountExersices: TextView = view.findViewById(R.id.exercisesCountTextView)
        val workoutImage: ImageView = view.findViewById(R.id.iv_exercise)
        val rV: RecyclerView = view.findViewById(R.id.rv_exercises)
        val startButton: Button = view.findViewById(R.id.btnStart)
        val favoriteButton: ImageButton = view.findViewById(R.id.ibFavorite)


        workout?.let {
            workoutTitle.text = it.title
            workoutTime.text = it.type
            workoutCountExersices.text = buildString {
                append(it.exercises.size)
                append(getExerciseEnding(it.exercises.size))
            }
            workoutImage.setImageResource(it.photo)
            rV.layoutManager = LinearLayoutManager(requireContext())
            rV.adapter = ExerciseAdapter(it.exercises)
        }

        startButton.setOnClickListener {
            val intent = Intent(context, WorkoutActivity::class.java)
            intent.putExtras(Bundle().apply {
                putParcelable("workoutKey", workout)
            })
            startActivity(intent)
        }

        favoriteButton.setOnClickListener {

        }

        return view
    }

    private fun getExerciseEnding(count: Int): String {
        return when {
            count % 100 in 11..14 -> " упражнений"
            count % 10 == 1 -> " упражнение"
            count % 10 in 2..4 -> " упражнения"
            else -> " упражнений"
        }
    }
}