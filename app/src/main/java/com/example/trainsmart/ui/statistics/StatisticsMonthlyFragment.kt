package com.example.trainsmart.ui.statistics

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.trainsmart.R
import com.google.firebase.auth.FirebaseAuth
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.Locale

class StatisticsMonthlyFragment : Fragment() {

    private var calendarView: CalendarView? = null
    private val viewModel: StatisticsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_statistics_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarView = view.findViewById(R.id.calendarView)
        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        viewModel.loadUserHistory(userId) {
            calendarView?.notifyCalendarChanged()
        }
        setupWeekCalendar()
    }

    private fun setupWeekCalendar() {
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)
        val endMonth = currentMonth.plusMonths(100)
        val firstDayOfWeek = firstDayOfWeekFromLocale()

        calendarView?.setup(startMonth, endMonth, firstDayOfWeek)
        calendarView?.scrollToMonth(currentMonth)

        class DayViewContainer(view: View) : ViewContainer(view) {
            val dayNameText: TextView = view.findViewById(R.id.calendarDayText)
            val dayNumberView = view.findViewById<TextView>(R.id.calendarDayText)
            lateinit var day: CalendarDay
        }

        class MonthHeaderContainer(view: View) : ViewContainer(view) {
            val titlesContainer: ViewGroup = view.findViewById(R.id.titlesContainer)
            val monthTitle: TextView = view.findViewById(R.id.monthTitle)
        }

        calendarView?.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {

                container.day = data
                container.dayNameText.text = data.date.dayOfMonth.toString()

                if (data.position == DayPosition.MonthDate) {

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

                    container.dayNameText.setTextColor(Color.BLACK)

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
                } else {
                    container.dayNameText.setTextColor(Color.TRANSPARENT)
                    container.dayNameText.setBackgroundColor(Color.TRANSPARENT)
                }


            }
        }

        calendarView?.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthHeaderContainer> {
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
                                }.toString()
                        }
                }
            }
        }
    }
}