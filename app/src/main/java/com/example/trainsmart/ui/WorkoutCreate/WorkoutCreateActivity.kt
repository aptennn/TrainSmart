package com.example.trainsmart.ui.WorkoutCreate

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trainsmart.R
import com.example.trainsmart.data.Workout
import com.example.trainsmart.databinding.ActivityWorkoutCreateBinding
import com.example.trainsmart.firestore.FireStoreClient
import com.example.trainsmart.ui.exercises.ExerciseListItemModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


class WorkoutCreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkoutCreateBinding
    private var selectedWorkoutType: String = ""
    private val exerciseModels = mutableListOf<ExerciseListItemModel>()
    private lateinit var exerciseListAdapter: ExerciseListAdapterCreate


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTypeButtons()
        setupRecyclerView()
        loadExercises()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewCreate.layoutManager = LinearLayoutManager(this)
        exerciseListAdapter = ExerciseListAdapterCreate(
            applicationContext,
            exerciseModels,
            { exercise -> }
        )
        binding.recyclerViewCreate.adapter = exerciseListAdapter
    }

    private fun loadExercises() {
        val firestoreClient = FireStoreClient()
        lifecycleScope.launch {
            firestoreClient.getAllExercises().collect { result ->
                exerciseModels.clear()
                result.filterNotNull().forEach { exercise ->
                    exerciseModels.add(
                        ExerciseListItemModel(
                            id = exercise.id,
                            name = exercise.name,
                            photo = exercise.photoUrl,
                            description = exercise.description,
                            technique = exercise.technique,
                            isSelected = false,
                            countSets = "",
                            countReps = ""
                        )
                    )
                }
                exerciseListAdapter.notifyDataSetChanged()
                setupSaveButton()
            }
        }
    }

    private fun setupSaveButton() {
        binding.buttonCreateFinish.setOnClickListener {
            val selectedExercises = exerciseModels.sortedBy { it.id }.filter { it.isSelected }
            val workoutName = binding.editTextWorkoutName.text.toString().trim()

            when {
                workoutName.isEmpty() -> showAlertDialog("Введите название тренировки")
                selectedExercises.isEmpty() -> showAlertDialog("Выберите минимум 1 упражнение")
                selectedWorkoutType.isEmpty() -> showAlertDialog("Выберите тип тренировки")
                selectedExercises.any { it.countSets.isBlank() || it.countReps.isBlank() } ->
                    showAlertDialog("Заполните подходы/повторы для выбранных упражнений")

                else -> saveWorkout(workoutName, selectedExercises)
            }
        }
    }

    private fun saveWorkout(name: String, exercises: List<ExerciseListItemModel>) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: run {
            showAlertDialog("Пользователь не авторизован")
            return
        }

        lifecycleScope.launch {
            FireStoreClient().getUser(userId).collect { user ->
                user?.let {
                    val exercisesMap = exercises.associate { ex ->
                        ex.id to "${ex.countSets}-${ex.countReps}"
                    }

                    val workout = Workout(
                        name = name,
                        photoUrl = "",
                        author = user.id,
                        exercises = exercisesMap,
                        type = selectedWorkoutType,
                        likes = mutableListOf()
                    )

                    FireStoreClient().saveWorkout(workout).collect { success ->
                        if (success) {
                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            showAlertDialog("Ошибка сохранения")
                        }
                    }
                }
            }
        }
    }

    private fun setupTypeButtons() {
        val btnUpper = binding.btnUpperBody
        val btnLower = binding.btnLowerBody

        btnUpper.setOnClickListener {
            selectedWorkoutType = "Верх тела"
            updateButtonStyle(btnUpper, btnLower)
        }

        btnLower.setOnClickListener {
            selectedWorkoutType = "Низ тела"
            updateButtonStyle(btnLower, btnUpper)
        }
    }

    private fun updateButtonStyle(selectedButton: Button, unselectedButton: Button) {
        selectedButton.setBackgroundResource(R.drawable.shape_button_selected)
        selectedButton.setTextColor(ContextCompat.getColor(this, R.color.white))

        unselectedButton.setBackgroundResource(R.drawable.shape_button_unselected)
        unselectedButton.setTextColor(ContextCompat.getColor(this, R.color.black))
    }

    private fun showAlertDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Ошибка")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}