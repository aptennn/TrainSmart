package com.example.trainsmart.ui.exercises

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExerciseListItemModel(val name: String, val photo: Int, val description: String, val technique: String, val countReps: String) : Parcelable