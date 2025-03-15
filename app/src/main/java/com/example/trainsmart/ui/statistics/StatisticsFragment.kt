package com.example.trainsmart.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.trainsmart.R
import com.example.trainsmart.databinding.FragmentStatisticsBinding
import java.util.Calendar
import androidx.core.graphics.toColorInt

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Настройка календаря текущей недели
        setupWeekCalendar()

        return root
    }

    private fun setupWeekCalendar() {
        val daysGrid = binding.daysGrid
        daysGrid.removeAllViews() // Очистка предыдущих данных

        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY // Начало недели с понедельника
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY) // Установка на понедельник

        for (i in 0 until 7) {
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val textView = TextView(requireContext()).apply {
                text = day.toString()
                gravity = Gravity.CENTER
                textSize = 18f
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    rightMargin = 8
                    columnSpec = GridLayout.spec(i, 1f)
                }
            }

            textView.setTextColor("#000614".toColorInt())
            textView.typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_bold)

            if (day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                textView.setBackgroundResource(R.drawable.shape_current_day_background)
            } else {
                textView.setBackgroundResource(R.drawable.shape_background_calendar_days)
            }

            daysGrid.addView(textView)
            calendar.add(Calendar.DAY_OF_MONTH, 1) // Переход к следующему дню
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}