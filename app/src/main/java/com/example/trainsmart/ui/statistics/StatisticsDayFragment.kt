package com.example.trainsmart.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trainsmart.R
import com.example.trainsmart.ui.workouts.ExerciseAdapter

class StatisticsDayFragment : Fragment() {
    private var workoutHistory: WorkoutHistory? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            workoutHistory = it.getParcelable("workoutHistoryKey")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_statistics_day, container, false)

        val workoutTime: TextView = view.findViewById(R.id.timeTextView)
        val workoutCountExersices: TextView = view.findViewById(R.id.exercisesCountTextView)
        val workoutImage: ImageView = view.findViewById(R.id.iv_exercise)
        val rV: RecyclerView = view.findViewById(R.id.rv_exercises)
        //val backButton: ImageButton = view.findViewById(R.id.ibBack)
        val workoutDate: TextView = view.findViewById(R.id.tvDate)
        val repeatButton: Button = view.findViewById(R.id.btnRepeat)

        workoutHistory?.workout?.let{
            //workoutTime.text = "${it.time} часа"
            workoutCountExersices.text = "${it.exercises.size} упражнения"
            workoutImage.setImageResource(it.photo)
            rV.layoutManager = LinearLayoutManager(requireContext())
            rV.adapter = ExerciseAdapter(it.exercises)
        }

        workoutHistory?.date.let {
            workoutDate.text = it;
        }

//        backButton.setOnClickListener {
//            requireActivity().onBackPressedDispatcher.onBackPressed()
//        }

        return view
    }
}