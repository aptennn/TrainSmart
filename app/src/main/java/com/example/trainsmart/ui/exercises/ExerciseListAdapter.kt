package com.example.trainsmart.ui.exercises

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainsmart.R
import java.util.Locale

class ExerciseListAdapter(private val context: Context, private val models: ArrayList<ExerciseListItemModel>, private val onClick: (ExerciseListItemModel) -> Unit) : RecyclerView.Adapter<ExerciseListAdapter.ViewHolder>() {
    private val filteredModels: ArrayList<ExerciseListItemModel> = ArrayList(models.size)

    init {
        filteredModels.addAll(models)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: CardView = itemView.findViewById(R.id.exercise_card)
        val label: TextView = itemView.findViewById(R.id.exercise_list_item_label)
        val photo: ImageView = itemView.findViewById(R.id.image_exercise_photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.exercise_list_row, parent, false)
        return ViewHolder(view)
    }

    fun setFilter(filter: String) {
        filteredModels.clear()
        filteredModels.addAll(models.filter { model -> model.name.lowercase(Locale.ROOT).contains(filter.lowercase(Locale.ROOT)) })
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return filteredModels.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.label.text = filteredModels[position].name
        if (filteredModels[position].photo != 0) {
            holder.photo.setImageResource(R.drawable.exercise1)
        }
        holder.card.setOnClickListener {
            onClick(filteredModels[position])
        }
    }
}