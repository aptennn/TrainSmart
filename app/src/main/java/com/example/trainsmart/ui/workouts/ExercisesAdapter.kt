package com.example.trainsmart.ui.workouts

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.trainsmart.R
import com.example.trainsmart.ui.exercises.ExerciseListItemModel

class ExerciseAdapter(private val exercises: List<ExerciseListItemModel>) :
    RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val exerciseName: TextView = view.findViewById(R.id.tv_title)
        val exerciseSets: TextView = view.findViewById(R.id.tvCountSets)
        val exerciseReps: TextView = view.findViewById(R.id.tvCountReps)
        val exerciseImage: ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise_card, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        with(holder) {
            exerciseName.text = exercise.name
            exerciseImage.load(exercise.photo)
            exerciseSets.text = buildString {
                append("-Подходы: ")
                append(exercise.countSets)
            }
            exerciseReps.text = buildString {
                append("-Повторения: ")
                append(exercise.countReps)
            }

            itemView.setOnClickListener {
                AlertDialog.Builder(itemView.context)
                    .setTitle(exercise.name)
                    .setMessage(exercise.technique)
                    .setPositiveButton("Закрыть") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        }

    }

    override fun getItemCount(): Int = exercises.size
}