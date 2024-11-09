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
            Workout("Грудь/Бицепс", 1.5, 25, 5),
            Workout("Спина/Трицепс", 1.5, 33, 15),
            Workout("Ноги/Плечи", 1.5, 19, 3),
            Workout("Верх тела", 2.0, 151, 55),
            Workout("Низ тела", 1.5, 4, 44),
        )

        rV.layoutManager = LinearLayoutManager(requireContext())
        rV.adapter = WorkoutsAdapter(
            items,
            onItemClickListener = { workout ->
                val bundle = Bundle().apply {
                    /*
                    putString("workoutTitle", workout.title)
                    putDouble("workoutTime", workout.time)
                    putInt("workoutLikes", workout.likes)
                    putInt("workoutDislikes", workout.dislikes)
                    */
                }
                findNavController().navigate(R.id.navigation_workout_details, bundle)
            },
            onSettingsClickListener = { workout ->
                val bundle = Bundle().apply {
                    /*
                    putString("workoutTitle", workout.title)
                    putDouble("workoutTime", workout.time)
                    putInt("workoutLikes", workout.likes)
                    putInt("workoutDislikes", workout.dislikes)
                    */
                }
                findNavController().navigate(R.id.navigation_workout_settings, bundle)
            },
            onLikeClickListener = { workout ->
                //workout.likes += 1 // Увеличиваем счётчик лайков
                //rV.adapter?.notifyItemChanged(items.indexOf(workout))
            },
            onDislikeClickListener = { workout ->
                //workout.dislikes += 1 // Увеличиваем счётчик дизлайков
                //rV.adapter?.notifyItemChanged(items.indexOf(workout))
            }
        )

        return view
    }
}