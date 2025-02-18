package com.example.trainsmart.ui.workouts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trainsmart.R

class WorkoutsAdapter(
    private val onItemClickListener: (Workout) -> Unit
) : ListAdapter<Workout, WorkoutsAdapter.WorkoutViewHolder>(WorkoutDiffCallback()) {

    inner class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val background: ImageView = itemView.findViewById(R.id.workoutImage)
        private val title: TextView = itemView.findViewById(R.id.workoutTitleTV)
        private val button: TextView = itemView.findViewById(R.id.workoutDescription)

        fun bind(workout: Workout) {
            title.text = workout.title
            background.setImageResource(workout.photo)
            button.setOnClickListener { onItemClickListener(workout) }
            itemView.setOnClickListener { onItemClickListener(workout) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout_card, parent, false)
        return WorkoutViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class WorkoutDiffCallback : DiffUtil.ItemCallback<Workout>() {
    override fun areItemsTheSame(oldItem: Workout, newItem: Workout): Boolean {
        return oldItem.title == newItem.title && oldItem.type == newItem.type
    }

    override fun areContentsTheSame(oldItem: Workout, newItem: Workout): Boolean {
        return oldItem == newItem
    }
}