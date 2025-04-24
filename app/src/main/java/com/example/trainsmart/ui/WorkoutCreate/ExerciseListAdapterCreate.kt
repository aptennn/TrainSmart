package com.example.trainsmart.ui.WorkoutCreate

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.trainsmart.R
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

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            }

            setsWatcher = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    originalModels[position].countSets = s?.toString() ?: ""
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

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