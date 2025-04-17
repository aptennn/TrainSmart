package com.example.trainsmart.ui.statistics

import android.os.Parcelable
import com.example.trainsmart.ui.workouts.Workout
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkoutHistory(val workout: Workout, val date: String) : Parcelable {}
