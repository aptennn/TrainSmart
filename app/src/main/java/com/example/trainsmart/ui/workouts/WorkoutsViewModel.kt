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

    fun loadWorkouts(callback: (Boolean) -> Unit) {
        isLoading = true
        viewModelScope.launch {
            try {
                workouts.clear()
                fireStoreClient.getAllWorkouts().collect { result ->
                    if (result.isNotEmpty()) {
                        result.values.forEach { workout ->
                            if (workout != null) {
                                val exercises = exercisesToList(workout.exercises)
                                workouts.add(
                                    UiWorkout(
                                        id = workout.id,
                                        title = workout.name,
                                        photo = R.drawable.exercise3,
                                        author = workout.author,
                                        exercises = exercises,
                                        type = workout.type,
                                        likes = workout.likes
                                    )
                                )
                            }
                        }
                    }
                    callback(true)
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