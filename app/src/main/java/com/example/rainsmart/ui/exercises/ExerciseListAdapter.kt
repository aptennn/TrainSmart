package com.example.rainsmart.ui.exercises

import android.content.Context
import android.graphics.drawable.Icon
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rainsmart.R

class ExerciseListAdapter(private val context: Context, private val models: ArrayList<ExerciseListItemModel>) : RecyclerView.Adapter<ExerciseListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val label: TextView = itemView.findViewById(R.id.exercise_list_item_label)
        val icon: ImageView = itemView.findViewById(R.id.exercise_list_item_icon)
        val description: TextView = itemView.findViewById(R.id.exercise_list_item_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.exercise_list_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return models.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.label.text = models[position].name
        holder.description.text = models[position].description
        if (models[position].icon != 0) {
            holder.icon.setImageIcon(Icon.createWithResource(context, models[position].icon))
        }
    }
}