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
    private val onDescriptionClickListener: (Workout) -> Unit,
) : RecyclerView.Adapter<WorkoutsAdapter.WorkoutViewHolder>() {

    private var filteredWorkouts: List<Workout> = workouts

    inner class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val background: ImageView = itemView.findViewById(R.id.workoutImage)
        val title: TextView = itemView.findViewById(R.id.workoutTitleTV)
        val button: Button = itemView.findViewById(R.id.workoutDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_workout_card, parent, false)
        return WorkoutViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = filteredWorkouts[position]
        holder.title.text = workout.title
        holder.button.setOnClickListener {
            onDescriptionClickListener(workout)
        }
        holder.background.setImageResource(workout.photo)
    }

    override fun getItemCount(): Int = filteredWorkouts.size

    fun updateData(newItems: List<Workout>) {
        this.workouts = newItems
        this.filteredWorkouts = newItems
        notifyDataSetChanged()
    }

    fun filterByType(type: Int?) {
        filteredWorkouts = if (type == null) {
            workouts
        } else {
            workouts.filter { it.type == type }
        }
        notifyDataSetChanged()
    }
}