package com.example.trainsmart.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trainsmart.R
import com.example.trainsmart.ui.workouts.WorkoutsAdapter
import com.example.trainsmart.ui.workouts.Workout as UiWorkout

class StatisticsDayFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WorkoutsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_statistics_day, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.workoutsRV)

        val workouts = arguments?.getParcelableArrayList<UiWorkout>("workoutsKey")

        adapter = WorkoutsAdapter { workout ->
            navigateToWorkoutDetails(workout)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        adapter.submitList(workouts)
    }

    private fun navigateToWorkoutDetails(workout: UiWorkout) {
        findNavController().navigate(
            R.id.navigation_workout_details,
            Bundle().apply { putParcelable("workoutKey", workout) }
        )
    }
}