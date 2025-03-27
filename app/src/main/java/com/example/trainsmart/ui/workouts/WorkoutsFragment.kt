package com.example.trainsmart.ui.workouts

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
import com.example.trainsmart.ui.exercises.ExerciseListItemModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import com.example.trainsmart.ui.workouts.Workout as UiWorkout
import com.example.trainsmart.data.Workout as DataWorkout
import kotlinx.coroutines.launch

class WorkoutsFragment : Fragment() {

    private var filterContainer: HorizontalScrollView? = null
    private var selectedButton: Button? = null
    private var searchField: EditText? = null
    private lateinit var adapter: WorkoutsAdapter
    private val originalItems = mutableListOf<UiWorkout>()
    private var currentFilter: Int? = null
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
        return inflater.inflate(R.layout.fragment_workouts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupRecyclerView(view)
        setupFilters(view)
        setupSearch()
    }

    override fun onDestroyView() {
        cleanupResources()
        super.onDestroyView()
    }

    private fun initializeData() {
        if (!isDataInitialized) {
            val firestoreClient = FireStoreClient()
            val uiWorkouts = mutableListOf<UiWorkout>()
            val workouts = mutableListOf<DataWorkout>()
            lifecycleScope.launch {
                firestoreClient.getAllWorkouts().collect { result ->
                    if (result.isNotEmpty()) {
                        for (workout in result) {
                            println(workout?.name)
                            workout?.let {
                                workouts.add(it)
                            }
                        }
                        for (workout in workouts) {
                            println(workout.duration)
                            uiWorkouts.add(
                                UiWorkout(
                                    workout.name,
                                    R.drawable.exercise3,
                                    workout.duration,

                                    exercisesToList(workout.exercises, firestoreClient),
                                    1
                                )
                            )
                        }
                    } else {
                        println("result is null")
                    }
                    println("SIZE UI LIST 333333333333:")
                    println(uiWorkouts.size)
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

//    private fun createWorkoutItems(): List<Workout> {
//        return listOf(
//            Workout(
//                "Программа для тренировки рук", R.drawable.image_arms_wrkt, 1.5,
//                listOf(
//                    ExerciseListItemModel(
//                        "Жим лёжа", R.drawable.exercise1,
//                        "Базовое упражнение, которое помогает развить мышцы груди, плеч и рук.",
//                        "1. Штанга находится на уровне глаз\n2. Занять такое положение на скамье...",
//                        "3 подхода по 6 повторений"
//                    ),
//                    ExerciseListItemModel(
//                        "Подъем штанги на бицепс", R.drawable.exercise6,
//                        "Cиловое изолированное упражнение, направленное на развитие бицепса плеча.",
//                        "1. Встаньте ровно со штангой в руках...",
//                        "5 подходов по 5 повторений"
//                    )
//                ),
//                1
//            ),
//            Workout(
//                "Программа для тренировки спины", R.drawable.image_back_wrkt, 1.5,
//                listOf(
//                    ExerciseListItemModel(
//                        "Тяга вертикального блока", R.drawable.exercise3,
//                        "Одно из фундаментальных упражнений для развития верхней части туловища",
//                        "1. Сядьте на скамью тренажёра...",
//                        "2 подхода по 10 повторений"
//                    ),
//                    ExerciseListItemModel(
//                        "Тяга горизонтального блока", R.drawable.exercise5,
//                        "Cиловое упражнение на развитие мышц спины...",
//                        "1. Расположитесь на сидении тренажёра...",
//                        "3 подхода по 11 повторений"
//                    )
//                ),
//                1
//            ),
//            Workout(
//                "Программа для тренировки ног", R.drawable.image_leg_wrkt, 1.5,
//                listOf(
//                    ExerciseListItemModel(
//                        "Приседания со штангой", R.drawable.exercise4,
//                        "Эффективное базовое упражнение...",
//                        "1. Плотно зафиксируйте ладони на грифе...",
//                        "4 подхода по 5 повторений"
//                    ),
//                    ExerciseListItemModel(
//                        "Становая тяга", R.drawable.exercise2,
//                        "Базовое упражнение силового тренинга...",
//                        "1. Располагаем ноги на ширине плеч...",
//                        "3 подхода по 8 повторений"
//                    )
//                ),
//                2
//            )
//        )
//    }

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
                                dataExs[i].name,
                                R.drawable.exercise3,
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
                    for (workout in result) {
                        println(workout?.name)
                        workout?.let {
                            workouts.add(it)
                        }
                        }
                    for (workout in workouts) {
                        uiWorkouts.add(
                            UiWorkout(
                                workout.name,
                                R.drawable.exercise3,
                                workout.duration,
                                exercisesToList(workout.exercises, firestoreClient),
                                1
                            )
                        )
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
            R.id.btnUpperBody -> 1
            R.id.btnLowerBody -> 2
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

    private fun UiWorkout.matchesFilter(filter: Int?): Boolean {
        return filter == null || type == filter
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