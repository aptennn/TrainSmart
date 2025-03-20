package com.example.trainsmart.ui.workouts

import android.os.Parcelable
import com.example.trainsmart.ui.exercises.ExerciseListItemModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Workout(val title: String, val photo: Int, val time: Double, val exercises: List<ExerciseListItemModel>, val type: Int) : Parcelable {}