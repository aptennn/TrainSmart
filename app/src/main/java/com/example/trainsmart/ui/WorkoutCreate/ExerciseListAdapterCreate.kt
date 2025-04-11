package com.example.trainsmart.ui.WorkoutCreate

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainsmart.R
import android.widget.EditText
import android.widget.LinearLayout
import coil.load
import com.example.trainsmart.ui.exercises.ExerciseListItemModel

// МЕНЯТЬ ОПАСНО, ИСПОЛЬЗУЕТСЯ В WORKOUTCREATE!!!
class ExerciseListAdapterCreate(
    private val context: Context,
    private val originalModels: MutableList<ExerciseListItemModel>,
    private val onClick: (ExerciseListItemModel) -> Unit
) : RecyclerView.Adapter<ExerciseListAdapterCreate.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivSelect: ImageView = itemView.findViewById(R.id.ivSelect)
        val llReps: LinearLayout = itemView.findViewById(R.id.llReps)
        val llApps: LinearLayout = itemView.findViewById(R.id.llApps)
        val twExersiceName: TextView = itemView.findViewById(R.id.twExersiceName)
        val iwExersiceImage: ImageView = itemView.findViewById(R.id.iwExersiceImage)
        val etExerciseReps: EditText = itemView.findViewById(R.id.etExerciseReps)
        val etExerciseApps: EditText = itemView.findViewById(R.id.etExerciseApps)

        private var repsWatcher: TextWatcher? = null
        private var setsWatcher: TextWatcher? = null

        fun bind(exercise: ExerciseListItemModel) {
            val position = adapterPosition.takeIf { it != RecyclerView.NO_POSITION }
                ?: return
            val currentExercise = originalModels[position]

            etExerciseReps.removeTextChangedListener(repsWatcher)
            etExerciseApps.removeTextChangedListener(setsWatcher)

            etExerciseReps.setText(currentExercise.countReps)
            etExerciseApps.setText(currentExercise.countSets)

            repsWatcher = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    originalModels[position].countReps = s?.toString() ?: ""
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            }

            setsWatcher = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    originalModels[position].countSets = s?.toString() ?: ""
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            }

            etExerciseReps.addTextChangedListener(repsWatcher)
            etExerciseApps.addTextChangedListener(setsWatcher)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_workout_create, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exercise = originalModels[position]

        with(holder) {
            bind(exercise)

            twExersiceName.text = exercise.name
            iwExersiceImage.load(exercise.photo)

            ivSelect.setImageResource(
                if (exercise.isSelected) R.drawable.ic_selected
                else R.drawable.ic_unselected
            )

            llReps.visibility = if (exercise.isSelected) View.VISIBLE else View.GONE
            llApps.visibility = if (exercise.isSelected) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                exercise.isSelected = !exercise.isSelected
                notifyItemChanged(position)
                onClick(exercise)
            }
        }
    }

    override fun getItemCount() = originalModels.size

    override fun getItemId(position: Int): Long {
        return originalModels[position].id.hashCode().toLong()
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }
}
//class ExerciseListAdapterCreate(
//    private val context: Context,
//    private val models: List<ExerciseListItemModel>,
//    private val onClick: (ExerciseListItemModel) -> Unit
//) : RecyclerView.Adapter<ExerciseListAdapterCreate.ViewHolder>() {
//    private val filteredModels: ArrayList<ExerciseListItemModel> = ArrayList(models)
//
//    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val card: CardView = itemView.findViewById(R.id.exercise_card)
//        val label: TextView = itemView.findViewById(R.id.exercise_list_item_label)
//        val photo: ImageView = itemView.findViewById(R.id.image_exercise_photo)
//
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val inflater = LayoutInflater.from(context)
//        val view = inflater.inflate(R.layout.exercise_list_row, parent, false)
//        return ViewHolder(view)
//    }
//
//    fun setFilter(filter: String) {
//        filteredModels.clear()
//        filteredModels.addAll(models.filter { model -> model.name.lowercase(Locale.ROOT).contains(filter.lowercase(Locale.ROOT)) })
//        notifyDataSetChanged()
//    }
//
//    override fun getItemCount(): Int {
//        return filteredModels.size
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val exercise = filteredModels[position]
//        holder.label.text = exercise.name
//
//        holder.photo.load(filteredModels[position].photo)
//
////        if (exercise.photo != 0) {
////            val TAG = this.javaClass.simpleName
////            Log.i(TAG, position.toString())
////
////            when (position) {
////                0 -> holder.photo.setImageResource(R.drawable.exercise1)
////                1 -> holder.photo.setImageResource(R.drawable.exercise2)
////                2 -> holder.photo.setImageResource(R.drawable.exercise3)
////                3 -> holder.photo.setImageResource(R.drawable.exercise4)
////                4 -> holder.photo.setImageResource(R.drawable.exercise5)
////                5 -> holder.photo.setImageResource(R.drawable.exercise6)
////            }
////        }
//
//        // Изменяем фон при выборе
//        val selectedColor = ContextCompat.getColor(context, R.color.black)
//        val defaultColor = ContextCompat.getColor(context, R.color.white)
//        //holder.card.setCardBackgroundColor(if (exercise.isSelected) selectedColor else defaultColor)
//        holder.checkIcon.visibility = if (exercise.isSelected) View.VISIBLE else View.GONE
//        //holder.label.text = if (exercise.isSelected) "selectedColor" else "defaultColor"
//
//        holder.card.setOnClickListener {
//            exercise.isSelected = !exercise.isSelected
//            notifyItemChanged(position)
//            onClick(exercise)
//        }
//    }
//}


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
