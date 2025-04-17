package com.example.trainsmart.ui.exercises

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// МЕНЯТЬ ОПАСНО, ИСПОЛЬЗУЕТСЯ В WORKOUTCREATE!!!

@Parcelize
data class ExerciseListItemModel(val id: String, val name: String, val photo: String, val description: String, val technique: String, var countReps: String = "", var countSets: String = "", var isSelected: Boolean = false) : Parcelable
// isSelected - для выбора в workoutCreate