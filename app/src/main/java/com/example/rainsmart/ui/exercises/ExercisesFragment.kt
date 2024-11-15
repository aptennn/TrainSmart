package com.example.rainsmart.ui.exercises

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rainsmart.R
import com.example.rainsmart.databinding.FragmentExercisesBinding

class ExercisesFragment : Fragment() {

    private var _binding: FragmentExercisesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(ExercisesViewModel::class.java)

        _binding = FragmentExercisesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val exerciseList: RecyclerView = root.findViewById(R.id.exercise_list)
        val exerciseModels = ArrayList<ExerciseListItemModel>()
        exerciseModels.add(ExerciseListItemModel(
            "Жим лёжа", R.drawable.exercise1,
            "Базовое упражнение, которое помогает развить мышцы груди, плеч и рук.",
            "1. хихи-хаха\n2. мяу мяу\n3. гав гав\n4. чик чирик"))
        exerciseList.setAdapter(ExerciseListAdapter(requireContext(), exerciseModels, { exercise ->
            val arguments = Bundle().apply {
                putString("exerciseName", exercise.name)
                putInt("exercisePhoto", exercise.photo)
                putString("exerciseDescription", exercise.description)
                putString("exerciseTechnique", exercise.technique)
            }
            findNavController().navigate(R.id.navigation_exercise_details, arguments)
        }))
        exerciseList.layoutManager = LinearLayoutManager(context)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}