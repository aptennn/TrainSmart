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
import com.example.trainsmart.databinding.ActivityWorkoutCreateBinding
import com.example.trainsmart.firestore.FireStoreClient
import com.example.trainsmart.ui.exercises.ExerciseListItemModel
import kotlinx.coroutines.launch
import com.example.trainsmart.data.Workout


class WorkoutCreateActivity : AppCompatActivity() {
    //private lateinit var recyclerView: RecyclerView
    //private lateinit var adapter: CreateViewModel
    // private val repository = FirebaseRepository()
    //private val database = FirebaseDatabase.getInstance().getReference("basic-exercises")
    //private lateinit var appBarConfiguration: AppBarConfiguration

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
//        setupTypeButtons()
//        val exerciseList: RecyclerView = findViewById(R.id.recyclerViewCreate)
//        val firestoreClient = FireStoreClient()
//        var exercises = mutableListOf<Exercise>()
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.title = "Создание Тренировки"
//
//        // переписать по человечески !!! (карутина в карутине)!
//        lifecycleScope.launch {
//            firestoreClient.getAllExercises().collect { result ->
//                // Start Animation
//
//                if (result.isNotEmpty()) {
//                    println(909)
//                    for (exercise in result)
//                        exercise?.let {
//                            exercises.add(it)
////                            println(exercise.name)
////                            println(exercises.size)
//                        }
//
//                    for (exercise in exercises) {
////                        println(133)
////                        println(exercise.id)
////                        println(exercises.size)
////                        println(exercise.name)
//                        exerciseModels.add(
//                            ExerciseListItemModel(
//                                exercise.id,
//                                exercise.name,
//                                exercise.photoUrl,
//                                exercise.description,
//                                exercise.technique,
//                            )
//                        )
//                        //exerciseListAdapter.notifyDataSetChanged()
//                    }
//                } else {
//                    println("result is null")
//                }
//
//                println("SIZE:")
//                println(exerciseModels.size)
//
//                var exerciseListAdapter =
//                    ExerciseListAdapterCreate(applicationContext, exerciseModels, { exercise ->
//                        var arguments = Bundle().apply {
//                            putString("exerciseName", exercise.name)
//                            putString("exercisePhoto", exercise.photo)
//                            putString("exerciseDescription", exercise.description)
//                            putString("exerciseTechnique", exercise.technique)
//                        }
//                        //findNavController().navigate(R.id.navigation_exercise_details, arguments)
//                    })
//
//                exerciseListAdapter.notifyDataSetChanged()
//                exerciseList.setAdapter(exerciseListAdapter)
//                exerciseList.layoutManager = LinearLayoutManager(applicationContext)
//
//                binding.buttonCreateFinish.setOnClickListener {
//                    val selectedExercises = exerciseModels.filter { it.isSelected }
//                    val workoutName = binding.editTextWorkoutName.text.toString().trim()
//                    val workoutDescription =
//                        binding.editTextWorkoutDescription.text.toString().trim()
//
//                    // Добавлена проверка типа тренировки
//                    if (workoutName.isEmpty() || selectedExercises.isEmpty() || selectedWorkoutType.isEmpty()) {
//                        showAlertDialog("Заполните все обязательные поля!")
//                        return@setOnClickListener
//                    }
//
//                    val exercisesMap = selectedExercises.associate { exercise ->
//                        exercise.id to "${exercise.countSets}-${exercise.countReps}"
//                    }
//                    val workout = Workout(
//                        name = workoutName,
//                        photoUrl = "",
//                        duration = "30 минут",
//                        exercises = exercisesMap,
//                        type = selectedWorkoutType // Используем выбранный тип
//                    )
//
//                    lifecycleScope.launch {
//                        firestoreClient.saveWorkout(workout).collect { success ->
//                            if (success) {
//                                finish()
//                            } else {
//                                showAlertDialog("Ошибка сохранения")
//                            }
//                        }
//                    }
//                }
//            }
//
//        }
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
        val exercisesMap = exercises.associate {
            it.id to "${it.countSets}-${it.countReps}"
        }

        val workout = Workout(
            name = name,
            photoUrl = "",
            duration = "30 минут",
            exercises = exercisesMap,
            type = selectedWorkoutType
        )

        lifecycleScope.launch {
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

//        recyclerView = findViewById(R.id.recyclerViewCreate)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        adapter = CreateViewModel(emptyList())
//        recyclerView.adapter = adapter


//setSupportActionBar(binding.toolbar)

/*val navController = findNavController(R.id.nav_host_fragment_content_workout_create)
appBarConfiguration = AppBarConfiguration(navController.graph)
setupActionBarWithNavController(navController, appBarConfiguration)

binding.fab.setOnClickListener { view ->
    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        .setAction("Action", null)
        .setAnchorView(R.id.fab).show()
}*/

/*private fun fetchData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val itemsList = mutableListOf<ExerciseListItemModel>()
                for (data in snapshot.children) {
                    val item = data.getValue(ExerciseListItemModel::class.java)
                    if (item != null) itemsList.add(item)
                }
                adapter = CreateViewModel(itemsList)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Ошибка: ${error.message}")
            }
        })
    }*/


/*override fun onSupportNavigateUp(): Boolean {
    val navController = findNavController(R.id.nav_host_fragment_content_workout_create)
    return navController.navigateUp(appBarConfiguration)
            || super.onSupportNavigateUp()
}*/