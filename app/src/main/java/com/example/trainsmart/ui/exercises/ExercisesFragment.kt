package com.example.trainsmart.ui.exercises

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trainsmart.R
import com.example.trainsmart.data.Exercise
import com.example.trainsmart.data.User
import com.example.trainsmart.databinding.FragmentExercisesBinding
import com.example.trainsmart.firestore.FireStoreClient
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlin.coroutines.suspendCoroutine
import android.widget.HorizontalScrollView
import android.widget.ImageButton

class ExercisesFragment : Fragment() {

    private var _binding: FragmentExercisesBinding? = null
    private var filterContainer: HorizontalScrollView? = null
    private var selectedButton: Button? = null
    private var currentFilter: String? = null
    private var currentQuery: String = ""
    private lateinit var originalExerciseList: MutableList<ExerciseListItemModel>
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

        val firestoreClient = FireStoreClient()

        var exercises = mutableListOf<Exercise>()

        val exerciseModels = mutableListOf<ExerciseListItemModel>()

        lifecycleScope.launch {
            firestoreClient.getAllExercises().collect { result ->
                // Start Animation


                if (result.isNotEmpty()) {
                    println(909)
                    for (exercise in result)
                        exercise?.let { exercises.add(it)
                            println(exercise.name)
                            println(exercises.size)
                        }
                    for (exercise in exercises) {
                        //println(133)
                        //println(exercises.size)
                        //println(exercise.name)
                        exerciseModels.add(
                            ExerciseListItemModel(
                                "",
                                exercise.name,
                                exercise.photoUrl,
                                exercise.description,
                                exercise.technique,
                                "3 подхода по 6 повторений"
                            )
                        )
                        //exerciseListAdapter.notifyDataSetChanged()
                    }
                }
                else{
                    println("result is null")
                }

                println("SIZE:")
                println(exerciseModels.size)

                var exerciseListAdapter = ExerciseListAdapter(requireContext(), exerciseModels, { exercise ->
                    var arguments = Bundle().apply {
                        putString("exerciseName", exercise.name)
                        putString("exercisePhoto", exercise.photo)
                        putString("exerciseDescription", exercise.description)
                        putString("exerciseTechnique", exercise.technique)
                    }
                    findNavController().navigate(R.id.navigation_exercise_details, arguments)
                })
                exerciseListAdapter.notifyDataSetChanged()
                exerciseList.setAdapter(exerciseListAdapter)
                exerciseList.layoutManager = LinearLayoutManager(context)

                // Finish Animation

                val searchBox: EditText = root.findViewById(R.id.searchField)
                searchBox.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        exerciseListAdapter.setFilter(s.toString())
                    }
                })
            }


        }


        //exerciseListAdapter.notifyDataSetChanged()
        //exerciseListAdapter.setFilter(" ")


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        originalExerciseList = mutableListOf()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}