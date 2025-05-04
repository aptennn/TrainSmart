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
import com.example.trainsmart.firestore.FireStoreClient

class WorkoutsWithTimeAdapter(
    private val onItemClickListener: (WorkoutWithTime) -> Unit
) : ListAdapter<WorkoutWithTime, WorkoutsWithTimeAdapter.WorkoutViewHolder>(WorkoutWithTimeDiffCallback()) {

    inner class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val background: ImageView = itemView.findViewById(R.id.workoutImage)
        private val title: TextView = itemView.findViewById(R.id.workoutTitleTV)
        private val likes: TextView = itemView.findViewById(R.id.workoutLikesTV)
        private val author: TextView = itemView.findViewById(R.id.workoutAuthorTV)
        private val duration: TextView = itemView.findViewById(R.id.workoutDurationTV)

        fun bind(item: WorkoutWithTime) {
            val workout = item.workout
            val durationText = item.duration

            likes.text = workout.likes.size.toString()

            FireStoreClient().getUserNew(workout.author) { user ->
                val nick = user?.username ?: "TrainSmart"
                author.text = "Автор: $nick"
            }

            title.text = workout.title
            background.setImageResource(workout.photo)

            duration.text = "Время: $durationText"

            itemView.setOnClickListener { onItemClickListener(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout_card_statistics, parent, false)
        return WorkoutViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class WorkoutWithTimeDiffCallback : DiffUtil.ItemCallback<WorkoutWithTime>() {
    override fun areItemsTheSame(oldItem: WorkoutWithTime, newItem: WorkoutWithTime): Boolean {
        return oldItem.workout.id == newItem.workout.id && oldItem.duration == newItem.duration
    }

    override fun areContentsTheSame(oldItem: WorkoutWithTime, newItem: WorkoutWithTime): Boolean {
        return oldItem == newItem
    }
}
