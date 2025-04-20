package com.example.trainsmart.ui.workouts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainsmart.R
import com.example.trainsmart.firestore.FireStoreClient
import com.example.trainsmart.ui.exercises.ExerciseListItemModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.example.trainsmart.ui.workouts.Workout as UiWorkout

class WorkoutsViewModel : ViewModel() {
    private val fireStoreClient = FireStoreClient()
    var workouts = mutableListOf<UiWorkout>()
    var isLoading = false

//    private fun createWorkoutItems(): List<UiWorkout> {
//        val firestoreClient = FireStoreClient()
//        val uiWorkouts = mutableListOf<UiWorkout>()
//        val workouts = mutableListOf<DataWorkout>()
//        lifecycleScope.launch {
//            firestoreClient.getAllWorkouts().collect { result ->
//                if (result.isNotEmpty()) {
//                    for (idWorkout in result) {
//                        val workout = idWorkout.value
//                        val idW = idWorkout.key
//                        if (workout != null)
//                            uiWorkouts.add(
//                                UiWorkout(
//                                    id = idW,
//                                    title = workout.name,
//                                    photo = R.drawable.exercise3,
//                                    time = workout.duration,
//                                    exercises = exercisesToList(
//                                        workout.exercises,
//                                        firestoreClient
//                                    ),
//                                    type = workout.type,
//                                    likes = workout.likes
//                                )
//                            )
//                        else
//                            println("ERROR! GOT NULL OR DEFECTED WORKOUT")
//                    }
//                } else {
//                    println("result is null")
//                }
//                println("SIZE UI LIST:")
//                println(uiWorkouts.size)
//            }
//        }
//        return uiWorkouts
//    }

    fun loadWorkouts(callback: (Boolean) -> Unit) {
        isLoading = true
        viewModelScope.launch {
            try {
                fireStoreClient.getAllWorkouts().collect { result ->
                    if (result.isNotEmpty()) {
                        for (idWorkout in result) {
                            val workout = idWorkout.value
                            val idW = idWorkout.key
                            if (workout != null)
                                workouts.add(
                                    UiWorkout(
                                        id = idW,
                                        title = workout.name,
                                        photo = R.drawable.exercise3,
                                        time = workout.duration,
                                        exercises = exercisesToList(workout.exercises),
                                        type = workout.type,
                                        likes = workout.likes
                                    )
                                )
                        }
                    }
                }
            } catch (_: Exception) {
                callback(false)
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun exercisesToList(exercises: Map<String, String>) =
        FireStoreClient().getExercisesByIds(exercises.keys.toList())
            .first()
            ?.filterNotNull()
            ?.mapNotNull { exercise ->
                exercises[exercise.id]?.let { setsReps ->
                    ExerciseListItemModel(
                        id = exercise.id,
                        name = exercise.name,
                        photo = exercise.photoUrl,
                        description = exercise.description,
                        technique = exercise.technique,
                        countSets = setsReps.split("-")[0],
                        countReps = setsReps.split("-")[1]
                    )
                }
            } ?: emptyList()
}