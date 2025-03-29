package com.example.trainsmart.ui.WorkoutCreate

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
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.example.trainsmart.ui.exercises.ExerciseListItemModel

// МЕНЯТЬ ОПАСНО, ИСПОЛЬЗУЕТСЯ В WORKOUTCREATE!!!
class ExerciseListAdapterCreate(
    private val context: Context,
    private val models: List<ExerciseListItemModel>,
    private val onClick: (ExerciseListItemModel) -> Unit
) : RecyclerView.Adapter<ExerciseListAdapterCreate.ViewHolder>() {
    private val filteredModels: ArrayList<ExerciseListItemModel> = ArrayList(models)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: CardView = itemView.findViewById(R.id.exercise_card)
        val label: TextView = itemView.findViewById(R.id.exercise_list_item_label)
        val photo: ImageView = itemView.findViewById(R.id.image_exercise_photo)
        val checkIcon: ImageView = itemView.findViewById(R.id.check_icon)

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
        val exercise = filteredModels[position]
        holder.label.text = exercise.name

        if (exercise.photo != 0) {
            val TAG = this.javaClass.simpleName
            Log.i(TAG, position.toString())

            when (position) {
                0 -> holder.photo.setImageResource(R.drawable.exercise1)
                1 -> holder.photo.setImageResource(R.drawable.exercise2)
                2 -> holder.photo.setImageResource(R.drawable.exercise3)
                3 -> holder.photo.setImageResource(R.drawable.exercise4)
                4 -> holder.photo.setImageResource(R.drawable.exercise5)
                5 -> holder.photo.setImageResource(R.drawable.exercise6)
            }
        }

        // Изменяем фон при выборе
        val selectedColor = ContextCompat.getColor(context, R.color.black)
        val defaultColor = ContextCompat.getColor(context, R.color.white)
        //holder.card.setCardBackgroundColor(if (exercise.isSelected) selectedColor else defaultColor)
        holder.checkIcon.visibility = if (exercise.isSelected) View.VISIBLE else View.GONE
        //holder.label.text = if (exercise.isSelected) "selectedColor" else "defaultColor"

        holder.card.setOnClickListener {
            exercise.isSelected = !exercise.isSelected
            notifyItemChanged(position)
            onClick(exercise)
        }
    }
}


/*
public class ExerciseListAdapterCreate(
    private val context: Context,
    private val exercises: MutableList<ExerciseListItemModel>,
    private val onItemClick: (ExerciseListItemModel) -> Unit
) : RecyclerView.Adapter<ExerciseListAdapterCreate.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val exerciseName: TextView = view.findViewById(R.id.tv_title)
        //private val exerciseImage: ImageView = view.findViewById(R.id.exercise_image)
        private val itemLayout: View = view.findViewById(R.id.recyclerViewCreate) // Корневой layout элемента

        fun bind(exercise: ExerciseListItemModel) {
            exerciseName.text = exercise.name
            //exerciseImage.setImageResource(exercise.photo)

            val selectedColor = ContextCompat.getColor(context, R.color.black)
            val defaultColor = ContextCompat.getColor(context, R.color.white)

            // Меняем фон в зависимости от выбора
            itemLayout.setBackgroundColor(
                if (exercise.isSelected) selectedColor else defaultColor
            )


            // Обработчик клика по элементу
            itemView.setOnClickListener {
                exercise.isSelected = !exercise.isSelected
                notifyItemChanged(adapterPosition) // Обновляем элемент
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(exercises[position])
    }

    override fun getItemCount(): Int = exercises.size
}*/
