package com.example.trainsmart.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainsmart.R
import com.example.trainsmart.firestore.FireStoreClient
import com.example.trainsmart.ui.exercises.ExerciseListItemModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import com.example.trainsmart.ui.workouts.Workout as UiWorkout

class StatisticsViewModel : ViewModel() {
    private val fireStoreClient = FireStoreClient()
    var isLoading = false
    var history = mutableMapOf<String, List<String>>()
    var workoutsForDate = mutableListOf<UiWorkout>()
    val trainingDates = mutableSetOf<LocalDate>()
    var totalTrainings = 0
    var recordStreak = 0

    fun loadUserHistory(userId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                fireStoreClient.getHistoryByUserId(userId).collect { historyMap ->
                    history.clear()
                    trainingDates.clear()

                    historyMap?.let {
                        history.putAll(it)
                        processTrainingDates()
                        calculateStats()
                        callback(true)
                    } ?: run {
                        callback(false)
                    }
                }
            } catch (_: Exception) {
                callback(false)
            }
        }
    }

    private fun processTrainingDates() {
        trainingDates.clear()
        history.keys.forEach { dateStr ->
            runCatching {
                LocalDate.parse(dateStr)
            }.onSuccess { date ->
                trainingDates.add(date)
            }
        }
    }

    private fun calculateStats() {
        totalTrainings = history.values.sumOf { it.size }

        val sortedDates = trainingDates.sorted()
        var currentStreak = 0
        recordStreak = 0
        var previousDate: LocalDate? = null

        for (date in sortedDates) {
            if (previousDate == null || previousDate.plusDays(1) == date) {
                currentStreak++
            } else {
                currentStreak = 1
            }

            if (currentStreak > recordStreak) {
                recordStreak = currentStreak
            }
            previousDate = date
        }
    }

    fun loadWorkoutsForDate(date: String, callback: (Boolean) -> Unit) {
        val workoutIds = history[date] ?: emptyList()
        isLoading = true
        viewModelScope.launch {
            try {
                workoutsForDate.clear()
                fireStoreClient.getWorkoutsByIds(workoutIds).collect { workouts ->
                    workouts.forEach { workout ->
                        val exercises = exercisesToList(workout.exercises)
                        workoutsForDate.add(
                            UiWorkout(
                                id = workout.id,
                                title = workout.name,
                                photo = R.drawable.exercise3,
                                author = workout.author,
                                exercises = exercises,
                                type = workout.type,
                                likes = workout.likes,
                                dislikes = workout.dislikes
                            )
                        )
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
        fireStoreClient.getExercisesByIds(exercises.keys.toList())
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