package com.example.trainsmart.ui.exercises

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trainsmart.R
import com.example.trainsmart.databinding.FragmentExercisesBinding
import com.example.trainsmart.firestore.FireStoreClient
import kotlinx.coroutines.launch
import android.widget.ProgressBar

class ExercisesFragment : Fragment() {

    private var _binding: FragmentExercisesBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressBar: ProgressBar
    private lateinit var exerciseList: RecyclerView
    private lateinit var exerciseModels: MutableList<ExerciseListItemModel>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExercisesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        loadExercises()
    }

    private fun setupViews() {
        progressBar = binding.root.findViewById(R.id.progressBar)
        exerciseList = binding.exerciseList
        exerciseModels = mutableListOf()

        // Начальная настройка видимости
        progressBar.visibility = View.VISIBLE
        exerciseList.visibility = View.GONE
    }

    private fun loadExercises() {
        lifecycleScope.launch {
            try {
                FireStoreClient().getAllExercises().collect { result ->
                    // Start Animation
                    showLoading()

                    if (result.isNotEmpty()) {
                        exerciseModels.clear()
                        result.filterNotNull().forEach { exercise ->
                            exerciseModels.add(
                                ExerciseListItemModel(
                                    id = exercise.id,
                                    name = exercise.name,
                                    photo = exercise.photoUrl,
                                    description = exercise.description,
                                    technique = exercise.technique,
                                    countReps = "3 подхода по 6 повторений"
                                )
                            )
                        }

                        setupRecyclerView()
                    } else {
                        showEmptyState()
                    }

                    // Finish Animation
                    hideLoading()
                }
            } catch (e: Exception) {
                hideLoading()
            }
        }
    }

    private fun setupRecyclerView() {
        val adapter = ExerciseListAdapter(
            requireContext(),
            exerciseModels,
            { exercise ->
                findNavController().navigate(
                    R.id.navigation_exercise_details,
                    Bundle().apply {
                        putString("exerciseName", exercise.name)
                        putString("exercisePhoto", exercise.photo)
                        putString("exerciseDescription", exercise.description)
                        putString("exerciseTechnique", exercise.technique)
                    }
                )
            }
        )

        exerciseList.layoutManager = LinearLayoutManager(context)
        exerciseList.adapter = adapter
        setupSearch(adapter)
    }

    private fun setupSearch(adapter: ExerciseListAdapter) {
        binding.searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                adapter.setFilter(s.toString())
            }
        })
    }

    private fun showLoading() {
        activity?.runOnUiThread {
            progressBar.visibility = View.VISIBLE
            exerciseList.visibility = View.GONE
        }
    }

    private fun hideLoading() {
        activity?.runOnUiThread {
            progressBar.visibility = View.GONE
            exerciseList.visibility = View.VISIBLE
        }
    }

    private fun showEmptyState() {
        activity?.runOnUiThread {
            Toast.makeText(context, "Нет доступных упражнений", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}