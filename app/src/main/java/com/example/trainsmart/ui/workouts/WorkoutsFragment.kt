package com.example.trainsmart.ui.workouts

import android.app.AlertDialog
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
import com.example.trainsmart.R
import com.example.trainsmart.ui.WorkoutCreate.WorkoutCreateActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.trainsmart.ui.workouts.Workout as UiWorkout


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
    private var selectedAuthorButton: Button? = null
    private var currentAuthorFilter: String? = null
    private var currentFavoritesFilter: Boolean = false

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

        val btnFavorites = view.findViewById<Button>(R.id.btnFavorite)
        btnFavorites.setOnClickListener { handleFavoritesFilterClick(btnFavorites) }

        val btnAll = view.findViewById<Button>(R.id.btnAllTypes)
        val btnUpper = view.findViewById<Button>(R.id.btnUpperBody)
        val btnLower = view.findViewById<Button>(R.id.btnLowerBody)
        selectedButton = btnAll
        listOf(btnAll, btnUpper, btnLower).forEach { button ->
            button?.setOnClickListener { handleTypeFilterClick(button) }
        }

        val btnAllAuthors = view.findViewById<Button>(R.id.btnAllAuthors)
        btnAllAuthors.setOnClickListener {
            currentAuthorFilter = null
            applyFilters()
            updateAllButtonsStyles()
        }
        val btnBasic = view.findViewById<Button>(R.id.btnBasic)
        val btnUser = view.findViewById<Button>(R.id.btnUser)
        val btnOwn = view.findViewById<Button>(R.id.btnOwn)
        selectedAuthorButton = btnAll
        listOf(btnBasic, btnUser, btnOwn).forEach { button ->
            button.setOnClickListener { handleAuthorFilterClick(button) }
        }

        currentQuery = ""
        searchField?.setText("")
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
        selectedAuthorButton = null
        currentFavoritesFilter = false

        currentFilter = null
        currentAuthorFilter = null
        currentQuery = ""
        isFilterVisible = false

        applyFilters()
        adapter.submitList(emptyList())
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        if (needRefresh) {
            needRefresh = false
            loadData()
        }
        searchField?.setText("")
        applyFilters()
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

    private fun updateUI() {
        val newList = viewModel.workouts.distinctBy { it.id }
        originalItems.clear()
        originalItems.addAll(newList)
        applyFilters()
        buttonCreate.visibility = View.VISIBLE
        workoutsList.visibility = View.VISIBLE
    }

    private fun handleTypeFilterClick(button: Button) {
        when (button.id) {
            R.id.btnAllTypes -> {
                currentFilter = null
            }
            R.id.btnUpperBody -> currentFilter = "Верх тела"
            R.id.btnLowerBody -> currentFilter = "Низ тела"
        }
        applyFilters()
        updateAllButtonsStyles()
    }


    private fun handleFavoritesFilterClick(button: Button) {
        currentFavoritesFilter = !currentFavoritesFilter
        applyFilters()
    }

    private fun handleAuthorFilterClick(button: Button) {
        when (button.id) {
            R.id.btnAllAuthors -> {
                currentAuthorFilter = null
            }
            R.id.btnBasic -> currentAuthorFilter = "basic"
            R.id.btnUser -> currentAuthorFilter = "users"
            R.id.btnOwn -> currentAuthorFilter = "own"
        }
        applyFilters()
        updateAllButtonsStyles()
    }

    private fun applyFilters() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val filtered = viewModel.workouts
            .filter {
                it.matchesQuery(currentQuery) &&
                        it.matchesTypeFilter(currentFilter) &&
                        it.matchesAuthorFilter(currentAuthorFilter) &&
                        (!currentFavoritesFilter || (currentUserId != null && it.likes.contains(
                            currentUserId
                        )))
            }
        adapter.submitList(filtered)
        updateAllButtonsStyles()
    }

    private fun UiWorkout.matchesTypeFilter(filter: String?): Boolean {
        return filter == null || type.equals(filter, ignoreCase = true)
    }

    private fun UiWorkout.matchesQuery(query: String): Boolean {
        return title.lowercase().contains(query.lowercase())
    }

    private fun UiWorkout.matchesAuthorFilter(filter: String?): Boolean {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        return when (filter) {
            "basic" -> author == "basic"
            "users" -> author != "basic" && author != currentUserId
            "own" -> author == currentUserId
            else -> true
        }
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

    private fun updateButtonStyles(
        buttons: List<Button>,
        isSelected: (Button) -> Boolean
    ) {
        buttons.forEach { button ->
            val selected = isSelected(button)
            button.setBackgroundResource(
                if (selected) R.drawable.shape_button_selected
                else R.drawable.shape_button_unselected
            )
            button.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (selected) R.color.white else R.color.black
                )
            )
        }
    }

    private fun updateAllButtonsStyles() {
        val typeButtons = listOf(
            requireView().findViewById<Button>(R.id.btnAllTypes),
            requireView().findViewById<Button>(R.id.btnUpperBody),
            requireView().findViewById<Button>(R.id.btnLowerBody)
        )
        updateButtonStyles(typeButtons) { button ->
            when (button.id) {
                R.id.btnAllTypes -> currentFilter == null
                R.id.btnUpperBody -> currentFilter == "Верх тела"
                R.id.btnLowerBody -> currentFilter == "Низ тела"
                else -> false
            }
        }

        val authorButtons = listOf(
            requireView().findViewById<Button>(R.id.btnAllAuthors),
            requireView().findViewById<Button>(R.id.btnBasic),
            requireView().findViewById<Button>(R.id.btnUser),
            requireView().findViewById<Button>(R.id.btnOwn)
        )
        updateButtonStyles(authorButtons) { button ->
            when (button.id) {
                R.id.btnAllAuthors -> currentAuthorFilter == null
                R.id.btnBasic -> currentAuthorFilter == "basic"
                R.id.btnUser -> currentAuthorFilter == "users"
                R.id.btnOwn -> currentAuthorFilter == "own"
                else -> false
            }
        }

        // Обновление кнопки избранного
        val favoritesButton = requireView().findViewById<Button>(R.id.btnFavorite)
        updateButtonStyles(listOf(favoritesButton)) { currentFavoritesFilter }
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