package com.example.trainsmart.ui.WorkoutCreate

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trainsmart.R
import com.example.trainsmart.data.Exercise
import com.example.trainsmart.databinding.ActivityWorkoutCreateBinding
import com.example.trainsmart.firestore.FireStoreClient
import com.example.trainsmart.ui.exercises.ExerciseListAdapter
import com.example.trainsmart.ui.WorkoutCreate.ExerciseListAdapterCreate
import com.example.trainsmart.ui.exercises.ExerciseListItemModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import com.example.trainsmart.databinding.FragmentExercisesBinding
import com.example.trainsmart.data.Workout


class WorkoutCreateActivity : AppCompatActivity() {
    //private lateinit var recyclerView: RecyclerView
    //private lateinit var adapter: CreateViewModel
   // private val repository = FirebaseRepository()
    //private val database = FirebaseDatabase.getInstance().getReference("basic-exercises")


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityWorkoutCreateBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWorkoutCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val exerciseList: RecyclerView = findViewById(R.id.recyclerViewCreate)

        val firestoreClient = FireStoreClient()

        var exercises = mutableListOf<Exercise>()

        val exerciseModels = mutableListOf<ExerciseListItemModel>()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Создание Тренировки"

        // переписать по человечески !!! (карутина в карутине)!
        lifecycleScope.launch {
            firestoreClient.getAllExercises().collect { result ->
                // Start Animation

                if (result.isNotEmpty()) {
                    println(909)
                    for (exercise in result)
                        exercise?.let {
                            exercises.add(it)
//                            println(exercise.name)
//                            println(exercises.size)
                    }

                    for (exercise in exercises) {
//                        println(133)
//                        println(exercise.id)
//                        println(exercises.size)
//                        println(exercise.name)
                        exerciseModels.add(
                            ExerciseListItemModel(
                                exercise.id,
                                exercise.name,
                                exercise.photoUrl,
                                exercise.description,
                                exercise.technique,
                                "3 подхода по 6 повторений"
                            )
                        )
                        //exerciseListAdapter.notifyDataSetChanged()
                    }
                } else {
                    println("result is null")
                }

                println("SIZE:")
                println(exerciseModels.size)

                    var exerciseListAdapter =
                    ExerciseListAdapterCreate(applicationContext, exerciseModels, { exercise ->
                        var arguments = Bundle().apply {
                            putString("exerciseName", exercise.name)
                            putString("exercisePhoto", exercise.photo)
                            putString("exerciseDescription", exercise.description)
                            putString("exerciseTechnique", exercise.technique)
                        }
                        //findNavController().navigate(R.id.navigation_exercise_details, arguments)
                    })

                exerciseListAdapter.notifyDataSetChanged()
                exerciseList.setAdapter(exerciseListAdapter)
                exerciseList.layoutManager = LinearLayoutManager(applicationContext)

                binding.buttonCreateFinish.setOnClickListener {

                    val selectedExercises = exerciseModels.filter { it.isSelected }.map { it.id }
                    val workoutName = binding.editTextWorkoutName.text.toString().trim()
                    val workoutDescription = binding.editTextWorkoutDescription.text.toString().trim()

                    if (workoutName.isEmpty()) {
                        println("Введите название тренировки!")
                        showAlertDialog()
                        return@setOnClickListener
                    }

                    if(selectedExercises.isEmpty()){
                        showAlertDialog()
                    }

                    // Формируем Map с упражнениями (значение по умолчанию "3x10")
                    val exercisesMap = selectedExercises.associateWith { "3-10" }

                    val workout = Workout(
                        name = workoutName,
                        photoUrl = "", // Если у тренировки нет фото, можно оставить пустым
                        duration = "30 минут", // Можно заменить на вводимое пользователем значение
                        exercises = exercisesMap,
                        type = "Силовая" // Можно сделать выбор типа тренировки
                    )

                    lifecycleScope.launch {
                        firestoreClient.saveWorkout(workout).collect { success ->
                            if (success) {
                                println("✅ Тренировка '$workoutName' сохранена в Firestore!")
                                finish() // Закрываем Activity после успешного сохранения
                            } else {
                                println("❌ Ошибка при сохранении тренировки")
                            }
                        }
                    }
                }
            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed() // Закрывает Activity или возвращает назад
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAlertDialog() {

        val builder = AlertDialog.Builder(this)

        builder.setTitle("Неправильные данные")

            .setMessage("Введите имя и выберите упражнения")

            .setPositiveButton("OK") { dialog, which ->

                // Действие при нажатии на кнопку OK\
                dialog.dismiss()

            }

        val alertDialog = builder.create()

        alertDialog.show()

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