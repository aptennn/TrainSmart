package com.example.trainsmart.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.trainsmart.R
import com.example.trainsmart.databinding.FragmentStatisticsHistoryBinding
import com.example.trainsmart.ui.exercises.ExerciseListItemModel
import com.example.trainsmart.ui.workouts.Workout
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.Locale

class StatisticsHistoryFragment : Fragment() {

    private var _binding: FragmentStatisticsHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        binding.ibBack.setOnClickListener {
//            requireActivity().onBackPressedDispatcher.onBackPressed()
//        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupWeekCalendar()
    }

    private fun setupWeekCalendar() {
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)
        val endMonth = currentMonth.plusMonths(100)
        val firstDayOfWeek = firstDayOfWeekFromLocale()

        binding.calendarView.setup(startMonth, endMonth, firstDayOfWeek)
        binding.calendarView.scrollToMonth(currentMonth)

        class DayViewContainer(view: View) : ViewContainer(view) {
            val textView: TextView = view.findViewById(R.id.calendarDayText)
            lateinit var day: CalendarDay

            init {
                view.setOnClickListener {
                    if (day.date == LocalDate.now()) {
                        // Only use month dates
                        findNavController().navigate(
                            R.id.navigation_statistics_day_history,
                            Bundle().apply {
                                val workoutHistory = WorkoutHistory(Workout(
                                    "Программа для тренировки спины", R.drawable.image_back_wrkt, 1.5.toString(),
                                    listOf(
                                        ExerciseListItemModel(
                                            "2",
                                            "Тяга вертикального блока", "https://ksd-images.lt/display/aikido/cache/1298f905f591174c3d942bbe9371f10e.jpeg",
                                            "Одно из фундаментальных упражнений для развития верхней части туловища",
                                            "1. Сядьте на скамью тренажёра...",
                                            "2 подхода по 10 повторений"
                                        ),
                                        ExerciseListItemModel(

                                            "2",
                                            "Тяга горизонтального блока", "https://ksd-images.lt/display/aikido/cache/1298f905f591174c3d942bbe9371f10e.jpeg",
                                            "Cиловое упражнение на развитие мышц спины...",
                                            "1. Расположитесь на сидении тренажёра...",
                                            "3 подхода по 11 повторений"
                                        )
                                    ),
                                    "Верх тела"
                                ), day.date.toString())
                                putParcelable("workoutHistoryKey", workoutHistory)
                            }
                        )
                    }
                    // Use the CalendarDay associated with this container.

                }
            }
        }

        class MonthHeaderContainer(view: View) : ViewContainer(view) {
            val titlesContainer: ViewGroup = view.findViewById(R.id.titlesContainer)
            val monthTitle: TextView = view.findViewById(R.id.monthTitle)
        }

        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {

                container.day = data
                container.textView.text = data.date.dayOfMonth.toString()

                if (data.position == DayPosition.MonthDate) {
                    container.textView.setTextColor(Color.BLACK)
                    if (data.date == LocalDate.now()) {
                        container.textView.setBackgroundResource(R.drawable.shape_background_current_day)
                        container.textView.setTextColor(Color.WHITE)
                    } else {
                        container.textView.setBackgroundResource(R.drawable.shape_background_calendar_days)
                    }
                } else {
                    container.textView.setTextColor(Color.TRANSPARENT)
                    container.textView.setBackgroundColor(Color.TRANSPARENT)
                }
            }
        }

        binding.calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthHeaderContainer> {
            override fun create(view: View) = MonthHeaderContainer(view)
            override fun bind(container: MonthHeaderContainer, data: CalendarMonth) {
                val russianMonthsMap = (1L..12L).associateWith {
                    when (it) {
                        1L -> "Январь"
                        2L -> "Февраль"
                        3L -> "Март"
                        4L -> "Апрель"
                        5L -> "Май"
                        6L -> "Июнь"
                        7L -> "Июль"
                        8L -> "Август"
                        9L -> "Сентябрь"
                        10L -> "Октябрь"
                        11L -> "Ноябрь"
                        12L -> "Декабрь"
                        else -> ""
                    }
                }
                container.monthTitle.text = data.yearMonth.format(
                    DateTimeFormatterBuilder()
                        .appendText(ChronoField.MONTH_OF_YEAR, russianMonthsMap)
                        .appendLiteral(" ")
                        .appendPattern("yyyy")
                        .toFormatter()
                        .withLocale(Locale("ru", "RU"))
                )

                if (container.titlesContainer.tag == null) {
                    container.titlesContainer.tag = data.yearMonth
                    container.titlesContainer.children.map { it as TextView }
                        .forEachIndexed { index, textView ->
                            val dayOfWeek = data.weekDays.first().first().date.dayOfWeek
                            val daysOfWeek = daysOfWeek(dayOfWeek)
                            textView.text =
                                when (daysOfWeek[index]) {
                                    DayOfWeek.MONDAY -> "Пн"
                                    DayOfWeek.TUESDAY -> "Вт"
                                    DayOfWeek.WEDNESDAY -> "Ср"
                                    DayOfWeek.THURSDAY -> "Чт"
                                    DayOfWeek.FRIDAY -> "Пт"
                                    DayOfWeek.SATURDAY -> "Сб"
                                    DayOfWeek.SUNDAY -> "Вс"
                                    else -> ""
                                }.toString()
                        }
                }
            }
        }
    }
}