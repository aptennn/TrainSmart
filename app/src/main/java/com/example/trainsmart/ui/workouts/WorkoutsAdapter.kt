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

class WorkoutsAdapter(
    private val onItemClickListener: (Workout) -> Unit
) : ListAdapter<Workout, WorkoutsAdapter.WorkoutViewHolder>(WorkoutDiffCallback()) {

    inner class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val background: ImageView = itemView.findViewById(R.id.workoutImage)
        private val title: TextView = itemView.findViewById(R.id.workoutTitleTV)
        private val likes: TextView = itemView.findViewById(R.id.workoutLikesTV)
        private val dislikes: TextView = itemView.findViewById(R.id.workoutDisLikesTV)
        private val author: TextView = itemView.findViewById(R.id.workoutAuthorTV)

        fun bind(workout: Workout) {
            likes.text = buildString {
                append(workout.likes.size)
            }

            dislikes.text = buildString {
                append(workout.dislikes.size)
            }

            var nick = ""
            FireStoreClient().getUserNew(workout.author){ user ->

                    nick = user?.username.toString()
                    println("Username: $nick")

                    author.text = buildString {
                        append("Автор: ")

                        if(nick == "null")
                            append("TrainSmart")
                        else
                            append(nick)

                    }

                }
            title.text = workout.title
            background.setImageResource(R.drawable.exercise1)


            val imageNumber = workout.photo

            // Проверяем, если преобразование прошло успешно
            if (imageNumber != null && imageNumber in 1..6) {
                // Используем конструкцию when, чтобы обработать разные случаи
                when (imageNumber) {
                    1 -> {
                        println("Вы выбрали первое изображение.")
                        // Добавь действия для первого изображения
                        background.setImageResource(R.drawable.exercise1)
                    }
                    2 -> {
                        println("Вы выбрали второе изображение.")
                        background.setImageResource(R.drawable.exercise2)
                        // Добавь действия для второго изображения
                    }
                    3 -> {
                        println("Вы выбрали третье изображение.")
                        // Добавь действия для третьего изображения
                        background.setImageResource(R.drawable.exercise3)
                    }
                    4 -> {
                        println("Вы выбрали четвёртое изображение.")
                        // Добавь действия для четвёртого изображения
                        background.setImageResource(R.drawable.exercise4)
                    }
                    5 -> {
                        println("Вы выбрали пятое изображение.")
                        // Добавь действия для пятого изображения
                        background.setImageResource(R.drawable.exercise5)
                    }
                    6 -> {
                        println("Вы выбрали шестое изображение.")
                        background.setImageResource(R.drawable.exercise6)
                        // Добавь действия для шестого изображения
                    }
                    else -> {
                        println("Неверный выбор.")
                    }
                }
            } else {
                println("Неверное значение: строка не может быть преобразована в число от 1 до 6.")
                println(workout.photo)
            }

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