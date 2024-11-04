package com.example.rainsmart.ui.exercises

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
        exerciseModels.add(ExerciseListItemModel("БЕГИТ", R.drawable.ic_dashboard_black_24dp, "ноги большой сильный"))
        exerciseModels.add(ExerciseListItemModel("ПРЕСС КАЧАТ", 0, "кубики выпирающи"))
        exerciseModels.add(ExerciseListItemModel("АНЖУМАНЯ", R.drawable.ic_home_black_24dp, "мощнейщи руки"))
        exerciseList.setAdapter(ExerciseListAdapter(requireContext(), exerciseModels))
        exerciseList.layoutManager = LinearLayoutManager(context)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}