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
        //val backButton: ImageButton = view.findViewById(R.id.ibBack)
        val favoriteButton: ImageButton = view.findViewById(R.id.ibFavorite)

        client = FireStoreClient()
        auth = Firebase.auth

        if (client.isLikedByMe(workout, auth.currentUser!!.uid))
            favoriteButton.setBackgroundResource(R.drawable.ic_favorite_on)
        else
            favoriteButton.setBackgroundResource(R.drawable.ic_favorite)

        workout?.let{
            workoutTitle.text = it.title
            workoutTime.text = it.type
            workoutCountExersices.text = "${it.exercises.size} упражнения"
            workoutImage.setImageResource(it.photo)
            rV.layoutManager = LinearLayoutManager(requireContext())
            rV.adapter = ExerciseAdapter(it.exercises)
        }

        startButton.setOnClickListener {
            val intent = Intent(context, WorkoutActivity::class.java)
            intent.putExtras(Bundle().apply {
                putParcelable("workoutKey", workout)
            })
            startActivity(intent)
        }

        favoriteButton.setOnClickListener {
            println("LIKED")
            if (workout != null) {
                val id = workout!!.id
                if (client.isLikedByMe(workout, auth.currentUser!!.uid)){
                    favoriteButton.setBackgroundResource(R.drawable.ic_favorite)
                    lifecycleScope.launch {
                        client.updateLikes(id, auth.currentUser!!.uid, false).collect { result ->
                            Log.d("Result", "Update success: $result")
                        }
                        workout!!.likes -= auth.currentUser!!.uid
                    }
                }
                else {
                    favoriteButton.setBackgroundResource(R.drawable.ic_favorite_on)
                    lifecycleScope.launch {
                        client.updateLikes(id, auth.currentUser!!.uid, true).collect { result ->
                            Log.d("Result", "Update success: $result")
                        }
                        workout!!.likes += auth.currentUser!!.uid
                    }
                }

            }
        }

//        backButton.setOnClickListener {
//            //getActivity().onBackPressed();
//            requireActivity().onBackPressedDispatcher.onBackPressed()
//        }


        return view
    }
}