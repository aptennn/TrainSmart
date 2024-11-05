package com.example.rainsmart.ui.workouts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rainsmart.R

class WorkoutsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workouts, container, false)
        val rV: RecyclerView = view.findViewById(R.id.workoutsRV)

        val items = listOf(
            Workout("Пресс", R.drawable.workout_abs, "Какое-то описание тренировки..."),
            Workout("Штанга", R.drawable.workout_barbell, "Какое-то описание тренировки..."),
            Workout("Беговые упражнения", R.drawable.workout_run, "Какое-то описание тренировки..."),
            Workout("Гимнастика", R.drawable.workout_gymnastic, "Какое-то описание тренировки..."),
            Workout("Разминка", R.drawable.workout_warmup, "Какое-то описание тренировки..."),
        )

        rV.layoutManager = LinearLayoutManager(requireContext())
        rV.adapter = WorkoutsAdapter(items) {
            findNavController().navigate(R.id.navigation_workout_details)
        }

        return view
    }
}