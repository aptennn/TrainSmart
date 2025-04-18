package com.example.trainsmart.ui.workouts

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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trainsmart.R
import com.example.trainsmart.data.Exercise
import com.example.trainsmart.firestore.FireStoreClient
import com.example.trainsmart.ui.WorkoutCreate.WorkoutCreateActivity
import com.example.trainsmart.ui.exercises.ExerciseListItemModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import com.example.trainsmart.data.Workout as DataWorkout
import com.example.trainsmart.ui.workouts.Workout as UiWorkout


class WorkoutsFragment : Fragment() {

    private var filterContainer: HorizontalScrollView? = null
    private var selectedButton: Button? = null
    private var searchField: EditText? = null
    private lateinit var adapter: WorkoutsAdapter
    private val originalItems = mutableListOf<UiWorkout>()
    private var currentFilter: String? = null
    private var currentQuery: String = ""
    private var isFilterVisible = false
    private var isDataInitialized = false
    private var searchTextWatcher: TextWatcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeData()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //(requireActivity() as AppCompatActivity).supportActionBar?.title = "Тренировки"
        /*val binding = WorkoutsFragment.inflate(inflater, container, false)

        // Find the button by ID
        val button = binding.buttonSwitchActivity // Assuming you use ViewBinding

        // Set an onClickListener to the button
        button.setOnClickListener {
            // Intent to start a new activity
            val intent = Intent(requireContext(), WorkoutCreateActivity::class.java)

            // Start the new activity
            startActivity(intent)
        }

        return binding.root*/

        return inflater.inflate(R.layout.fragment_workouts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupRecyclerView(view)
        setupFilters(view)
        setupSearch()

        //(requireActivity() as AppCompatActivity).supportActionBar?.title = "Тренировки"

        val button = view.findViewById<Button>(R.id.button_create)

        // Set an OnClickListener to the button
        button.setOnClickListener {
            // Create an Intent to start the new activity
            val intent = Intent(requireContext(), WorkoutCreateActivity::class.java)

            // Start the new activity
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        cleanupResources()
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        isDataInitialized = false
        initializeData()
    }


    private fun initializeData() {
        if (!isDataInitialized) {
            val firestoreClient = FireStoreClient()
            val uiWorkouts = mutableListOf<UiWorkout>()
            //val workouts = mutableListOf<DataWorkout>()
            lifecycleScope.launch {
                firestoreClient.getAllWorkouts().collect { result ->
                    if (result.isNotEmpty()) {
                        //workouts.clear()
                        uiWorkouts.clear()
                        originalItems.clear()
                        adapter.submitList(emptyList())

                        for (idWorkout in result) {
                            val workout = idWorkout.value
                            val idW = idWorkout.key
                            if (workout != null) {
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
                                println(workout.name)
                            }
                            else
                                println("ERROR! GOT NULL OR DEFECTED WORKOUT")
                        }
                    } else {
                        println("result is null")
                    }
                    println("SIZE UI LIST 333333333333:")
                    println(uiWorkouts.size)
                    originalItems.clear()
                    originalItems.addAll(uiWorkouts)
                    println()
                    isDataInitialized = true

                    val recyclerView = view?.findViewById<RecyclerView>(R.id.workoutsRV)
                    if (recyclerView != null) {
                        recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    }
                    adapter = WorkoutsAdapter { navigateToWorkoutDetails(it) }
                    if (recyclerView != null) {
                        recyclerView.adapter = adapter
                    }
                    adapter.submitList(originalItems)
                }

            }

        }
    }

    private fun initializeViews(view: View) {
        filterContainer = view.findViewById(R.id.filterContainer)
        searchField = view.findViewById(R.id.searchField)
        view.findViewById<ImageButton>(R.id.btnFilter).setOnClickListener { toggleFiltersVisibility() }
    }


    // ДИЧЬ!!!
    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.workoutsRV)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = WorkoutsAdapter { navigateToWorkoutDetails(it) }
        recyclerView.adapter = adapter
        adapter.submitList(originalItems)
    }

    private fun setupFilters(view: View) {
        val btnAll = view.findViewById<Button>(R.id.btnAllTypes)
        val btnUpper = view.findViewById<Button>(R.id.btnUpperBody)
        val btnLower = view.findViewById<Button>(R.id.btnLowerBody)

        selectedButton = btnAll
        updateButtonStyles(listOf(btnAll, btnUpper, btnLower), btnAll)

        listOf(btnAll, btnUpper, btnLower).forEach { button ->
            button?.setOnClickListener { handleFilterClick(button) }
        }
    }

    private fun setupSearch() {
        searchTextWatcher = createTextWatcher()
        searchField?.addTextChangedListener(searchTextWatcher)
    }

    private fun cleanupResources() {
        searchField?.removeTextChangedListener(searchTextWatcher)
        searchTextWatcher = null
        searchField?.setText("")
        filterContainer = null
        selectedButton = null
        // Сброс фильтров и состояния
        currentFilter = null
        currentQuery = ""
        isFilterVisible = false
        // Сброс стилей кнопок
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
    }

    private fun exercisesToList(exercises: Map<String, String>, firestoreClient: FireStoreClient): List<ExerciseListItemModel> {
        val uiExercisesList = mutableListOf<ExerciseListItemModel>()
        val exIds = exercises.map { it.key }
        val exReps = exercises.map { it.value }
        val dataExs = mutableListOf<Exercise>()

        lifecycleScope.async {
        for (exercise in exercises)
            firestoreClient.getExercisesByIds(exIds).collect { result ->
                if (result != null) {
                    for (ex in result)
                        ex?.let { dataExs.add(it)
                        }
                    for (i in 0 until dataExs.size) {
                        uiExercisesList.add(
                            ExerciseListItemModel(
                                dataExs[i].id,
                                dataExs[i].name,
                                dataExs[i].photoUrl,
                                dataExs[i].description,
                                dataExs[i].technique,
                                exReps[i]
                            )
                        )
                    }
                }



            }


        }
        //def.join()

        return uiExercisesList
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
                                    exercises = exercisesToList(workout.exercises, firestoreClient),
                                    type = workout.type,
                                    likes = workout.likes
                                )
                            )
                        else
                            println("ERROR! GOT NULL OR DEFECTED WORKOUT")
                    }
                }
                else{
                    println("result is null")
                }
                println("SIZE UI LIST:")
                println(uiWorkouts.size)
            }

        }

        return uiWorkouts
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
        val filtered = originalItems.filter {
            it.matchesQuery(currentQuery) && it.matchesFilter(currentFilter)
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
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }
}