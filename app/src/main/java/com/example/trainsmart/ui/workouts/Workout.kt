package com.example.trainsmart.ui.workouts

import android.os.Parcelable
import com.example.trainsmart.ui.exercises.ExerciseListItemModel
import kotlinx.parcelize.Parcelize
import java.util.Objects

@Parcelize
data class Workout(val title: String, val photo: Int, val time: String, val exercises: List<ExerciseListItemModel>, val type: Int) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Workout
        return title == other.title && type == other.type
    }

    override fun hashCode(): Int = Objects.hash(title, type)
}