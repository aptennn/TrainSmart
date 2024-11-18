package com.example.trainsmart.ui.workouts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainsmart.R

class WorkoutsAdapter(
    private var workouts: List<Workout>,
    private val onItemClickListener: (Workout) -> Unit,
    private val onSettingsClickListener: (Workout) -> Unit,
    private val onLikeClickListener: (Workout) -> Unit,
    private val onDislikeClickListener: (Workout) -> Unit
) : RecyclerView.Adapter<WorkoutsAdapter.WorkoutViewHolder>() {

    inner class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val time: TextView = itemView.findViewById(R.id.workoutTimeTV)
        val title: TextView = itemView.findViewById(R.id.workoutTitleTV)
        val likes: TextView = itemView.findViewById(R.id.workoutLikeTV)
        val dislikes: TextView = itemView.findViewById(R.id.workoutDislikeTV)
        val button: Button = itemView.findViewById(R.id.workoutSettingsB)
        private val likeIcon: ImageView = itemView.findViewById(R.id.workoutLikeIV)
        private val dislikeIcon: ImageView = itemView.findViewById(R.id.workoutDislikeIV)
        init {
            itemView.setOnClickListener {
                onItemClickListener(workouts[adapterPosition])
            }
            likeIcon.setOnClickListener {
                onLikeClickListener(workouts[adapterPosition])
            }
            dislikeIcon.setOnClickListener {
                onDislikeClickListener(workouts[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_workout_card, parent, false)
        return WorkoutViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workouts[position]
        holder.time.text = workout.time.toString()
        holder.title.text = workout.title
        holder.likes.text = workout.likes.toString()
        holder.dislikes.text = workout.dislikes.toString()
        holder.button.setOnClickListener {
            onSettingsClickListener(workout)
        }
    }

    override fun getItemCount(): Int = workouts.size
}