package com.example.trainsmart.ui.workouts

import android.app.AlertDialog
import com.example.trainsmart.R
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trainsmart.firestore.FireStoreClient
import com.example.trainsmart.ui.WorkoutCreate.WorkoutCreateActivity
import com.example.trainsmart.ui.workouts.Workout as UiWorkout
import com.example.trainsmart.data.Workout as DataWorkout


class WorkoutsFragment : Fragment() {

    private val viewModel: WorkoutsViewModel by viewModels()
    private var needRefresh = false
    private var progressBar: ProgressBar? = null
    private lateinit var workoutsList: RecyclerView
    private lateinit var buttonCreate: Button
    private var filterContainer: HorizontalScrollView? = null
    private var selectedButton: Button? = null
    private var searchField: EditText? = null
    private lateinit var adapter: WorkoutsAdapter
    private val originalItems = mutableListOf<UiWorkout>()
    private var currentFilter: String? = null
    private var currentQuery: String = ""
    private var isFilterVisible = false
    private var searchTextWatcher: TextWatcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_workouts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        workoutsList = view.findViewById<RecyclerView>(R.id.workoutsRV)
        workoutsList.layoutManager = LinearLayoutManager(requireContext())
        adapter = WorkoutsAdapter { navigateToWorkoutDetails(it) }
        workoutsList.adapter = adapter
        workoutsList.visibility = View.GONE
        adapter.submitList(originalItems)

        filterContainer = view.findViewById(R.id.filterContainer)
        searchField = view.findViewById(R.id.searchField)
        progressBar = view.findViewById(R.id.progressBar)
        view.findViewById<ImageButton>(R.id.btnFilter)
            .setOnClickListener { toggleFiltersVisibility() }

        val btnAll = view.findViewById<Button>(R.id.btnAllTypes)
        val btnUpper = view.findViewById<Button>(R.id.btnUpperBody)
        val btnLower = view.findViewById<Button>(R.id.btnLowerBody)
        selectedButton = btnAll
        updateButtonStyles(listOf(btnAll, btnUpper, btnLower), btnAll)
        listOf(btnAll, btnUpper, btnLower).forEach { button ->
            button?.setOnClickListener { handleFilterClick(button) }
        }

        searchTextWatcher = createTextWatcher()
        searchField?.addTextChangedListener(searchTextWatcher)

        buttonCreate = view.findViewById<Button>(R.id.button_create)
        buttonCreate.visibility = View.GONE

        if (viewModel.workouts.isEmpty()) {
            loadData()
        } else {
            updateUI()
        }

        buttonCreate.setOnClickListener {
            val intent = Intent(requireContext(), WorkoutCreateActivity::class.java)

            startActivity(intent)
            needRefresh = true
        }
    }

    override fun onDestroyView() {
        progressBar?.visibility = View.GONE
        searchField?.removeTextChangedListener(searchTextWatcher)
        searchTextWatcher = null
        searchField?.setText("")
        filterContainer = null
        selectedButton = null

        currentFilter = null
        currentQuery = ""
        isFilterVisible = false

        view?.findViewById<Button>(R.id.btnAllTypes)?.let { defaultButton ->
            updateButtonStyles(
                listOf(
                    view?.findViewById(R.id.btnAllTypes),
                    view?.findViewById(R.id.btnUpperBody),
                    view?.findViewById(R.id.btnLowerBody)
                ),
                defaultButton
            )
        }
        adapter.submitList(emptyList())
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        if (needRefresh) {
            needRefresh = false
            loadData()
        }
    }

    private fun loadData() {
        progressBar?.visibility = View.VISIBLE
        buttonCreate.visibility = View.GONE
        workoutsList.visibility = View.GONE

        viewModel.loadWorkouts { success ->
            activity?.runOnUiThread {
                progressBar?.visibility = View.GONE
                if (success) {
                    updateUI()
                } else {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Ошибка")
                        .setMessage("Не удалось загрузить данные")
                        .setPositiveButton("Повторить") { _, _ -> loadData() }
                        .setNegativeButton("Отмена", null)
                        .show()
                }
            }
        }
    }

    private fun createWorkoutItems(): List<UiWorkout> {
        val firestoreClient = FireStoreClient()
        val uiWorkouts = mutableListOf<UiWorkout>()
        val workouts = mutableListOf<DataWorkout>()
        lifecycleScope.launch {
            firestoreClient.getAllWorkouts().collect { result ->
                if (result.isNotEmpty()) {
                    for (idWorkout in result) {
                        val workout = idWorkout.value
                        val idW = idWorkout.key
                        if (workout != null)
                            uiWorkouts.add(
                                UiWorkout(
                                    id = idW,
                                    title = workout.name,
                                    photo = R.drawable.exercise3,
                                    time = workout.duration,
                                    exercises = exercisesToList(
                                        workout.exercises,
                                        firestoreClient
                                    ),
                                    type = workout.type,
                                    likes = workout.likes
                                )
                            )
                        else
                            println("ERROR! GOT NULL OR DEFECTED WORKOUT")
                    }
                } else {
                    println("result is null")
                }
                println("SIZE UI LIST:")
                println(uiWorkouts.size)
            }
        }
        return uiWorkouts
    }

    private fun updateUI() {
        originalItems.clear()
        originalItems.addAll(viewModel.workouts)
        applyFilters()
        buttonCreate.visibility = View.VISIBLE
        workoutsList.visibility = View.VISIBLE
    }

    private fun handleFilterClick(button: Button) {
        currentFilter = when (button.id) {
            R.id.btnUpperBody -> "Верх тела"
            R.id.btnLowerBody -> "Низ тела"
            else -> null
        }
        applyFilters()
        updateButtonStyles(
            listOf(
                requireView().findViewById(R.id.btnAllTypes),
                requireView().findViewById(R.id.btnUpperBody),
                requireView().findViewById(R.id.btnLowerBody)
            ),
            button
        )
    }

    private fun applyFilters() {
        val filtered = viewModel.workouts
            .filter {
                it.matchesQuery(currentQuery) &&
                        it.matchesFilter(currentFilter)
            }
        adapter.submitList(filtered)
    }

    private fun UiWorkout.matchesQuery(query: String): Boolean {
        return title.lowercase().contains(query.lowercase())
    }

    private fun UiWorkout.matchesFilter(filter: String?): Boolean {
        return filter == null || type.equals(filter, ignoreCase = true)
    }

    private fun navigateToWorkoutDetails(workout: UiWorkout) {
        findNavController().navigate(
            R.id.navigation_workout_details,
            Bundle().apply { putParcelable("workoutKey", workout) }
        )
    }

    private fun toggleFiltersVisibility() {
        filterContainer?.visibility = if (isFilterVisible) View.GONE else View.VISIBLE
        isFilterVisible = !isFilterVisible
    }

    private fun updateButtonStyles(buttons: List<Button?>, clickedButton: Button) {
        buttons.forEach { button ->
            button?.setBackgroundResource(
                if (button == clickedButton) R.drawable.shape_button_selected
                else R.drawable.shape_button_unselected
            )
            button?.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (button == clickedButton) R.color.white else R.color.black
                )
            )
        }
        selectedButton = clickedButton
    }

    private fun createTextWatcher() = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            currentQuery = s?.toString().orEmpty()
            applyFilters()
        }

        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }
}