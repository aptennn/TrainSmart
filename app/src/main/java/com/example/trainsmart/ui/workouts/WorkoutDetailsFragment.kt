package com.example.trainsmart.ui.workouts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trainsmart.R
import com.example.trainsmart.WorkoutActivity
import com.example.trainsmart.firestore.FireStoreClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WorkoutsDetailsFragment : Fragment() {

    private var workout: Workout? = null
    private lateinit var client: FireStoreClient
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        // ПОМЕНЯЛ с savedInstanceState на null чтоб без наложения
        super.onCreate(null)
        arguments?.let {
            workout = it.getParcelable("workoutKey")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_workout_details, container, false)

        val workoutTitle: TextView = view.findViewById(R.id.tvTitle)
        val workoutTime: TextView = view.findViewById(R.id.timeTextView)
        val workoutCountExersices: TextView = view.findViewById(R.id.exercisesCountTextView)
        val workoutImage: ImageView = view.findViewById(R.id.iv_exercise)
        val rV: RecyclerView = view.findViewById(R.id.rv_exercises)
        val startButton: Button = view.findViewById(R.id.btnStart)
        val favoriteButton: ImageButton = view.findViewById(R.id.ibFavorite)
        val dislikeButton: ImageButton = view.findViewById(R.id.ibDislike)

        client = FireStoreClient()
        auth = Firebase.auth

        if (client.isLikedByMe(workout, auth.currentUser!!.uid) == "LIKED") {
            favoriteButton.setImageResource(R.drawable.ic_thumb_up)
            favoriteButton.setBackgroundResource(R.drawable.shape_bg_circle_blue)

            dislikeButton.setImageResource(R.drawable.ic_thumb_down)
            dislikeButton.setBackgroundResource(R.drawable.shape_bg_circle)

            println("LIKED !!!")

        }
        if (client.isLikedByMe(workout, auth.currentUser!!.uid) == "DISLIKED") {
            favoriteButton.setImageResource(R.drawable.ic_thumb_up)
            favoriteButton.setBackgroundResource(R.drawable.shape_bg_circle)

            dislikeButton.setImageResource(R.drawable.ic_thumb_down)
            dislikeButton.setBackgroundResource(R.drawable.shape_bg_circle_blue)

            println("DISLIKED !!!")
        }
        if (client.isLikedByMe(workout, auth.currentUser!!.uid) == "NONE") {
            favoriteButton.setImageResource(R.drawable.ic_thumb_up)
            favoriteButton.setBackgroundResource(R.drawable.shape_bg_circle)

            dislikeButton.setImageResource(R.drawable.ic_thumb_down)
            dislikeButton.setBackgroundResource(R.drawable.shape_bg_circle)

            println("NONE !!!")
        }


        workout?.let {
            workoutTitle.text = it.title
            workoutTime.text = it.type
            workoutCountExersices.text = buildString {
                append(it.exercises.size)
                append(getExerciseEnding(it.exercises.size))
            }
            workoutImage.setImageResource(R.drawable.exercise3)

            val imageNumber = it.photo


            if (imageNumber != null && imageNumber in 1..6) {
                // Используем конструкцию when, чтобы обработать разные случаи
                when (imageNumber) {
                    1 -> {
                        println("Вы выбрали первое изображение.")
                        // Добавь действия для первого изображения
                        workoutImage.setImageResource(R.drawable.exercise1)
                    }
                    2 -> {
                        println("Вы выбрали второе изображение.")
                        // Добавь действия для второго изображения
                        workoutImage.setImageResource(R.drawable.exercise2)
                    }
                    3 -> {
                        println("Вы выбрали третье изображение.")
                        // Добавь действия для третьего изображения
                        workoutImage.setImageResource(R.drawable.exercise3)
                    }
                    4 -> {
                        println("Вы выбрали четвёртое изображение.")
                        // Добавь действия для четвёртого изображения
                        workoutImage.setImageResource(R.drawable.exercise4)
                    }
                    5 -> {
                        println("Вы выбрали пятое изображение.")
                        // Добавь действия для пятого изображения
                        workoutImage.setImageResource(R.drawable.exercise5)
                    }
                    6 -> {
                        println("Вы выбрали шестое изображение.")
                        // Добавь действия для шестого изображения
                        workoutImage.setImageResource(R.drawable.exercise6)
                    }
                    else -> {
                        println("Неверный выбор.")
                    }
                }
            } else {
                println("Неверное значение: строка не может быть преобразована в число от 1 до 6.")
            }

            rV.layoutManager = LinearLayoutManager(requireContext())
            rV.adapter = ExerciseAdapter(it.exercises)
        }

        startButton.setOnClickListener {
            workout?.let { currentWorkout ->
                val userId = auth.currentUser?.uid ?: return@setOnClickListener
                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                lifecycleScope.launch {
                    client.addWorkoutToHistory(
                        userId = userId,
                        date = currentDate,
                        workoutId = currentWorkout.id
                    ).collect { success ->
                        if (success) {
                            Log.d("History", "Workout added to history")
                        } else {
                            Log.e("History", "Failed to add workout to history")
                        }
                    }
                }
            }

            val intent = Intent(context, WorkoutActivity::class.java)
            intent.putExtras(Bundle().apply {
                putParcelable("workoutKey", workout)
            })
            startActivity(intent)
        }



        favoriteButton.setOnClickListener {
           // println("LIKED")
            if (workout != null) {
                val id = workout!!.id
                if (client.isLikedByMe(workout, auth.currentUser!!.uid) == "LIKED") { // был лайк, снимаем его
                    favoriteButton.setImageResource(R.drawable.ic_thumb_up)
                    favoriteButton.setBackgroundResource(R.drawable.shape_bg_circle)
                    lifecycleScope.launch {
                        //client.updateLikes(id, auth.currentUser!!.uid, FireStoreClient.LikeType.UNLIKED).collect { result ->
                        //    Log.d("Result", "Update success: $result")
                        //}
                        Log.d("Result", "Start update likes")
                            //updateLikesFireAndForget(workoutId, uid, likeType)
                        val ss = client.updateLikesFireAndForget(id, auth.currentUser!!.uid, FireStoreClient.LikeType.UNLIKED)
                        Log.d("Result", "Finish update likes" + ss)
                        workout!!.likes -= auth.currentUser!!.uid
                    }
                } else if (client.isLikedByMe(workout, auth.currentUser!!.uid) == "DISLIKED") { // был дизлайк, снимаем его, ставим лайк
                        favoriteButton.setImageResource(R.drawable.ic_thumb_up)
                        favoriteButton.setBackgroundResource(R.drawable.shape_bg_circle_blue)

                        dislikeButton.setImageResource(R.drawable.ic_thumb_down)
                        dislikeButton.setBackgroundResource(R.drawable.shape_bg_circle)
                        lifecycleScope.launch {
//                            client.updateLikes(id, auth.currentUser!!.uid, FireStoreClient.LikeType.LIKED).collect { result ->
//                                Log.d("Result", "Update success: $result")
//                            }
                            Log.d("Result", "Start update likes")
                            val ss = client.updateLikesFireAndForget(id, auth.currentUser!!.uid, FireStoreClient.LikeType.LIKED)
                            Log.d("Result", "Finish update likes" + ss)
                            workout!!.likes += auth.currentUser!!.uid
                            workout!!.dislikes -= auth.currentUser!!.uid
                        }
                    }
                else if (client.isLikedByMe(workout, auth.currentUser!!.uid) == "NONE") {
                    favoriteButton.setImageResource(R.drawable.ic_thumb_up)
                    favoriteButton.setBackgroundResource(R.drawable.shape_bg_circle_blue)

                    lifecycleScope.launch {
//                        client.updateLikes(id, auth.currentUser!!.uid, FireStoreClient.LikeType.LIKED).collect { result ->
//                            Log.d("Result", "Update success: $result")
//                        }
                        Log.d("Result", "Start update likes")
                        val ss = client.updateLikesFireAndForget(id, auth.currentUser!!.uid, FireStoreClient.LikeType.LIKED)
                        Log.d("Result", "Finish update likes" + ss)
                        workout!!.likes += auth.currentUser!!.uid
                    }
                }
            }
        }


        dislikeButton.setOnClickListener {
            println("LIKED")
            if (workout != null) {
                val id = workout!!.id
                if (client.isLikedByMe(workout, auth.currentUser!!.uid) == "LIKED") { // был лайк, снимаем его, ставим дизлайк
                    dislikeButton.setImageResource(R.drawable.ic_thumb_down)
                    dislikeButton.setBackgroundResource(R.drawable.shape_bg_circle_blue)

                    favoriteButton.setImageResource(R.drawable.ic_thumb_up)
                    favoriteButton.setBackgroundResource(R.drawable.shape_bg_circle)
                    lifecycleScope.launch {
//                        client.updateLikes(id, auth.currentUser!!.uid, FireStoreClient.LikeType.DISLIKED).collect { result ->
//                            Log.d("Result", "Update success: $result")
//                        }

                        Log.d("Result", "Start update likes")
                        val ss = client.updateLikesFireAndForget(id, auth.currentUser!!.uid, FireStoreClient.LikeType.DISLIKED)
                        Log.d("Result", "Finish update likes" + ss)
                        workout!!.likes -= auth.currentUser!!.uid
                        workout!!.dislikes += auth.currentUser!!.uid
                    }
                } else if (client.isLikedByMe(workout, auth.currentUser!!.uid) == "DISLIKED") { // был дизлайк, снимаем его
                    dislikeButton.setImageResource(R.drawable.ic_thumb_down)
                    dislikeButton.setBackgroundResource(R.drawable.shape_bg_circle)

                    lifecycleScope.launch {
//                        client.updateLikes(id, auth.currentUser!!.uid, FireStoreClient.LikeType.UNDISLIKED).collect { result ->
//                            Log.d("Result", "Update success: $result")
//                        }
                        Log.d("Result", "Start update likes")
                        val ss = client.updateLikesFireAndForget(id, auth.currentUser!!.uid, FireStoreClient.LikeType.UNDISLIKED)
                        Log.d("Result", "Finish update likes" + ss)
                        workout!!.dislikes -= auth.currentUser!!.uid
                    }
                }
                else if (client.isLikedByMe(workout, auth.currentUser!!.uid) == "NONE") {
                    dislikeButton.setImageResource(R.drawable.ic_thumb_down)
                    dislikeButton.setBackgroundResource(R.drawable.shape_bg_circle_blue)

                    lifecycleScope.launch {
//                        client.updateLikes(id, auth.currentUser!!.uid, FireStoreClient.LikeType.DISLIKED).collect { result ->
//                            Log.d("Result", "Update success: $result")
//                        }
                        Log.d("Result", "Start update likes")
                        val ss = client.updateLikesFireAndForget(id, auth.currentUser!!.uid, FireStoreClient.LikeType.DISLIKED)
                        Log.d("Result", "Finish update likes" + ss)
                        workout!!.dislikes += auth.currentUser!!.uid
                    }
                }
            }
        }




        return view
    }

    private fun getExerciseEnding(count: Int): String {
        return when {
            count % 100 in 11..14 -> " упражнений"
            count % 10 == 1 -> " упражнение"
            count % 10 in 2..4 -> " упражнения"
            else -> " упражнений"
        }
    }
}