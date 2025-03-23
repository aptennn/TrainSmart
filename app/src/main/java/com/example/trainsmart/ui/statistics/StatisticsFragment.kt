package com.example.trainsmart.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.trainsmart.R
import com.example.trainsmart.databinding.FragmentStatisticsBinding
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekDayBinder
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.TVallTrainings.setOnClickListener {
            findNavController().navigate(R.id.action_navigationStatistics_to_navigationStatisticsHistory)
        }

        setupWeekCalendar()
    }

    private fun setupWeekCalendar() {
        val currentDate = LocalDate.now()
        val currentMonth = YearMonth.now()
        val startDate = currentMonth.minusMonths(100).atStartOfMonth()
        val endDate = currentMonth.plusMonths(100).atEndOfMonth()
        val firstDayOfWeek = firstDayOfWeekFromLocale()

        class DayViewContainer(view: View) : ViewContainer(view) {
            val dayNumberView = view.findViewById<TextView>(R.id.calendarDayText)
            val dayNameText = view.findViewById<TextView>(R.id.calendarDayName)

        }


            binding.weekCalendarView.setup(startDate, endDate, firstDayOfWeek)

        binding.weekCalendarView.scrollToDate(currentDate)

        binding.weekCalendarView.dayBinder = object : WeekDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)

            override fun bind(container: DayViewContainer, data: WeekDay) {
                container.dayNumberView.text = data.date.dayOfMonth.toString()

                container.dayNameText.text = when (data.date.dayOfWeek) {
                    DayOfWeek.MONDAY -> "Пн"
                    DayOfWeek.TUESDAY -> "Вт"
                    DayOfWeek.WEDNESDAY -> "Ср"
                    DayOfWeek.THURSDAY -> "Чт"
                    DayOfWeek.FRIDAY -> "Пт"
                    DayOfWeek.SATURDAY -> "Сб"
                    DayOfWeek.SUNDAY -> "Вс"
                    else -> ""
                }

                if (data.date == LocalDate.now()) {
                    container.dayNumberView.setBackgroundResource(R.drawable.shape_background_current_day)
                    container.dayNumberView.setTextColor(Color.WHITE)
                } else {
                    container.dayNumberView.setBackgroundResource(R.drawable.shape_background_calendar_days)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}