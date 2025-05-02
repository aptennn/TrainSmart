package com.example.trainsmart.ui.workouts

import android.os.Parcelable
import com.example.trainsmart.ui.exercises.ExerciseListItemModel
import kotlinx.parcelize.Parcelize
import java.util.Objects

@Parcelize
data class Workout(
    val id: String,
    val title: String,
    val photo: Int,
    val author: String,
    val exercises: List<ExerciseListItemModel>,
    val type: String,
    var likes: List<String>,
    var dislikes: List<String>
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Workout
        return title == other.title && type == other.type && author == other.author
    }

    override fun hashCode(): Int = Objects.hash(title, type)
}