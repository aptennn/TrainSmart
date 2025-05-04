package com.example.trainsmart.ui.workouts

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkoutWithTime(
    val workout: Workout,
    val duration: String
):Parcelable
