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
            favoriteButton.setImageResource(R.drawable.ic_favorite_white)
            favoriteButton.setBackgroundResource(R.drawable.shape_bg_circle_blue)

            dislikeButton.setImageResource(R.drawable.ic_favorite_black)
            dislikeButton.setBackgroundResource(R.drawable.shape_bg_circle)

            println("LIKED !!!")

        }
        else if (client.isLikedByMe(workout, auth.currentUser!!.uid) == "DISLIKED") {
            favoriteButton.setImageResource(R.drawable.ic_favorite_black)
            favoriteButton.setBackgroundResource(R.drawable.shape_bg_circle)

            dislikeButton.setImageResource(R.drawable.ic_favorite_white)
            dislikeButton.setBackgroundResource(R.drawable.shape_bg_circle_blue)

            println("DISLIKED !!!")
        }
        else if (client.isLikedByMe(workout, auth.currentUser!!.uid) == "NONE") {
            favoriteButton.setImageResource(R.drawable.ic_favorite_black)
            favoriteButton.setBackgroundResource(R.drawable.shape_bg_circle)

            dislikeButton.setImageResource(R.drawable.ic_favorite_black)
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
            workoutImage.setImageResource(it.photo)
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
                    favoriteButton.setImageResource(R.drawable.ic_favorite_black)
                    favoriteButton.setBackgroundResource(R.drawable.shape_bg_circle)
                    lifecycleScope.launch {
                        client.updateLikes(id, auth.currentUser!!.uid, FireStoreClient.LikeType.UNLIKED).collect { result ->
                            Log.d("Result", "Update success: $result")
                        }
                        workout!!.likes -= auth.currentUser!!.uid
                    }
                } else if (client.isLikedByMe(workout, auth.currentUser!!.uid) == "DISLIKED") { // был дизлайк, снимаем его, ставим лайк
                        favoriteButton.setImageResource(R.drawable.ic_favorite_white)
                        favoriteButton.setBackgroundResource(R.drawable.shape_bg_circle_blue)

                        dislikeButton.setImageResource(R.drawable.ic_favorite_black)
                        dislikeButton.setBackgroundResource(R.drawable.shape_bg_circle)
                        lifecycleScope.launch {
                            client.updateLikes(id, auth.currentUser!!.uid, FireStoreClient.LikeType.LIKED).collect { result ->
                                Log.d("Result", "Update success: $result")
                            }
                            workout!!.likes += auth.currentUser!!.uid
                            workout!!.dislikes -= auth.currentUser!!.uid
                        }
                    }
                else if (client.isLikedByMe(workout, auth.currentUser!!.uid) == "NONE") {
                    favoriteButton.setImageResource(R.drawable.ic_favorite_white)
                    favoriteButton.setBackgroundResource(R.drawable.shape_bg_circle_blue)

                    lifecycleScope.launch {
                        client.updateLikes(id, auth.currentUser!!.uid, FireStoreClient.LikeType.LIKED).collect { result ->
                            Log.d("Result", "Update success: $result")
                        }
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
                    dislikeButton.setImageResource(R.drawable.ic_favorite_white)
                    dislikeButton.setBackgroundResource(R.drawable.shape_bg_circle_blue)

                    favoriteButton.setImageResource(R.drawable.ic_favorite_black)
                    favoriteButton.setBackgroundResource(R.drawable.shape_bg_circle)
                    lifecycleScope.launch {
                        client.updateLikes(id, auth.currentUser!!.uid, FireStoreClient.LikeType.DISLIKED).collect { result ->
                            Log.d("Result", "Update success: $result")
                        }
                        workout!!.likes -= auth.currentUser!!.uid
                        workout!!.dislikes += auth.currentUser!!.uid
                    }
                } else if (client.isLikedByMe(workout, auth.currentUser!!.uid) == "DISLIKED") { // был дизлайк, снимаем его
                    dislikeButton.setImageResource(R.drawable.ic_favorite_black)
                    dislikeButton.setBackgroundResource(R.drawable.shape_bg_circle)

                    lifecycleScope.launch {
                        client.updateLikes(id, auth.currentUser!!.uid, FireStoreClient.LikeType.UNDISLIKED).collect { result ->
                            Log.d("Result", "Update success: $result")
                        }
                        workout!!.dislikes -= auth.currentUser!!.uid
                    }
                }
                else if (client.isLikedByMe(workout, auth.currentUser!!.uid) == "NONE") {
                    dislikeButton.setImageResource(R.drawable.ic_favorite_white)
                    dislikeButton.setBackgroundResource(R.drawable.shape_bg_circle_blue)

                    lifecycleScope.launch {
                        client.updateLikes(id, auth.currentUser!!.uid, FireStoreClient.LikeType.DISLIKED).collect { result ->
                            Log.d("Result", "Update success: $result")
                        }
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