package com.example.trainsmart.ui.statistics

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.trainsmart.R
import com.google.firebase.auth.FirebaseAuth
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekCalendarView
import com.kizitonwose.calendar.view.WeekDayBinder
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class StatisticsFragment : Fragment() {

    private var allTrainingsTV: TextView? = null
    private var trainsCountTV: TextView? = null
    private var trainsTV: TextView? = null
    private var recordTV: TextView? = null
    private var weekCalendarView: WeekCalendarView? = null
    private val viewModel: StatisticsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Статистика"

        trainsTV = view.findViewById(R.id.TVtains)
        allTrainingsTV = view.findViewById(R.id.TVallTrainings)
        recordTV = view.findViewById(R.id.TVrecord)
        trainsCountTV = view.findViewById(R.id.TVtrainsCount)
        weekCalendarView = view.findViewById(R.id.weekCalendarView)



        allTrainingsTV?.setOnClickListener {
            findNavController().navigate(R.id.action_navigationStatistics_to_navigationStatisticsHistory)
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        viewModel.loadUserHistory(userId) {
            trainsCountTV?.text = viewModel.totalTrainings.toString()
            trainsTV?.text = getWorkoutSuffix(viewModel.totalTrainings)
            recordTV?.text = buildString {
                append("Рекорд:\n")
                append(viewModel.recordStreak.toString())
                append(getDaySuffix(viewModel.recordStreak))
            }
            weekCalendarView?.notifyCalendarChanged()
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


        weekCalendarView?.setup(startDate, endDate, firstDayOfWeek)
        weekCalendarView?.scrollToDate(currentDate)

        weekCalendarView?.dayBinder = object : WeekDayBinder<DayViewContainer> {
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
                    if (viewModel.trainingDates.contains(data.date)) {
                        container.dayNumberView.setBackgroundResource(R.drawable.shape_background_trained_day)
                        container.dayNumberView.setTextColor(Color.BLACK)
                    } else {
                        container.dayNumberView.setBackgroundResource(R.drawable.shape_background_calendar_day)
                    }
                }

                container.dayNumberView.setOnClickListener {
                    val selectedDate = data.date
                    if (viewModel.trainingDates.contains(selectedDate)) {
                        viewModel.loadWorkoutsForDate(selectedDate.toString()) { success ->
                            if (success && viewModel.workoutsForDate.isNotEmpty()) {
                                val args = Bundle().apply {
                                    putParcelableArrayList(
                                        "workoutsKey",
                                        ArrayList(viewModel.workoutsForDate)
                                    )
                                }
                                findNavController().navigate(
                                    R.id.navigation_statistics_day_history,
                                    args
                                )
                            }
                        }
                    } else {
                        AlertDialog.Builder(requireContext())
                            .setMessage("В этот день тренировок не было")
                            .setPositiveButton("Закрыть") { dialog, _ -> dialog.dismiss() }
                            .show()
                    }
                }
            }
        }
    }

    private fun getDaySuffix(count: Int): String {
        return when {
            count % 100 in 11..14 -> " дней"
            count % 10 == 1 -> " день"
            count % 10 in 2..4 -> " дня"
            else -> " дней"
        }
    }

    private fun getWorkoutSuffix(count: Int): String {
        return when {
            count % 100 in 11..14 -> "Тренировок"
            count % 10 == 1 -> "Тренировка"
            count % 10 in 2..4 -> "Тренировки"
            else -> "Тренировок"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}