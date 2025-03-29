package com.example.trainsmart.ui.exercises

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import coil.load
import com.example.trainsmart.R

private const val ARG_EXERCISE_NAME = "exerciseName"
private const val ARG_EXERCISE_PHOTO = "exercisePhoto"
private const val ARG_EXERCISE_DESCRIPTION = "exerciseDescription"
private const val ARG_EXERCISE_TECHNIQUE = "exerciseTechnique"

class ExerciseDetailsFragment : Fragment() {
    private var exerciseName: String? = null
    private var exercisePhoto: String? = null
    private var exerciseDescription: String? = null
    private var exerciseTechnique: String? = null

    private var viewExerciseName: TextView? = null
    private var viewExercisePhoto: ImageView? = null
    private var viewExerciseDescription: TextView? = null
    private var viewExerciseTechnique: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            exerciseName = it.getString(ARG_EXERCISE_NAME)
            exercisePhoto = it.getString(ARG_EXERCISE_PHOTO)
            exerciseDescription = it.getString(ARG_EXERCISE_DESCRIPTION)
            exerciseTechnique = it.getString(ARG_EXERCISE_TECHNIQUE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_exercise_details, container, false)
        viewExerciseName = root.findViewById(R.id.text_exercise_name)
        viewExercisePhoto = root.findViewById(R.id.image_exercise_photo)
        viewExerciseDescription = root.findViewById(R.id.text_exercise_description)
        viewExerciseTechnique = root.findViewById(R.id.text_exercise_technique)

        viewExerciseName?.text = exerciseName
        //if (exercisePhoto != null)

        viewExercisePhoto?.load(exercisePhoto)

            // viewExercisePhoto?.setImageResource(exercisePhoto!!)
        viewExerciseDescription?.text = exerciseDescription
        viewExerciseTechnique?.text = exerciseTechnique
        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(name: String, photo: String, description: String, technique: String) =
            ExerciseDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_EXERCISE_NAME, name)
                    putString(ARG_EXERCISE_PHOTO, photo)
                    putString(ARG_EXERCISE_DESCRIPTION, description)
                    putString(ARG_EXERCISE_TECHNIQUE, technique)
                }
            }
    }
}