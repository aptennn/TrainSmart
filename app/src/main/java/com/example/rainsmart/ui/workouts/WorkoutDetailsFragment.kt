package com.example.rainsmart.ui.workouts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rainsmart.databinding.FragmentWorkoutDetailsBinding

class WorkoutsDetailsFragment : Fragment() {

    private var _binding: FragmentWorkoutDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutDetailsBinding.inflate(inflater, container, false)
        val view = binding.root

        val title = arguments?.getString("detail_title")
        val imageResId = arguments?.getInt("detail_image")
        val description = arguments?.getString("detail_description")

        binding.detailTitle.text = title
        binding.detailDescription.text = description
        imageResId?.let { binding.detailImage.setImageResource(it) }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}