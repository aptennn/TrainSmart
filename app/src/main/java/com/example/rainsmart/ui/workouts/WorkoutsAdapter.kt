package com.example.rainsmart.ui.workouts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rainsmart.R

class WorkoutsAdapter(
    private var workouts: List<Workout>,
    private val onItemClickListener: (Int) -> Unit
) : RecyclerView.Adapter<WorkoutsAdapter.WorkoutViewHolder>() {

    inner class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.workoutsTV)
        val image: ImageView = itemView.findViewById(R.id.workoutsIV)
        val button: Button = itemView.findViewById(R.id.workoutsB)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_workout_card, parent, false)
        return WorkoutViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workouts[position]
        holder.title.text = workout.title
        holder.image.setImageResource(workout.image)
        holder.button.setOnClickListener {
            onItemClickListener(position)
        }
    }

    override fun getItemCount(): Int = workouts.size
}