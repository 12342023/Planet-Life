package com.example.planetlife.ui.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planetlife.data.local.entity.PlanetEventEntity
import com.example.planetlife.data.repository.PlanetEventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

data class CalendarDayUi(
    val date: LocalDate?,
    val label: String,
    val isToday: Boolean = false,
    val isSelected: Boolean = false,
)

data class LogsUiState(
    val visibleMonth: YearMonth = YearMonth.now(),
    val monthTitle: String = "",
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedDateLabel: String = "",
    val monthDays: List<CalendarDayUi> = emptyList(),
    val events: List<PlanetEventEntity> = emptyList(),
    val isLoading: Boolean = true
)

class LogsViewModel(
    private val eventRepository: PlanetEventRepository
) : ViewModel() {

    private val visibleMonth = MutableStateFlow(YearMonth.now())
    private val selectedDate = MutableStateFlow(LocalDate.now())
    private val dateLabelFormatter = DateTimeFormatter.ofPattern("M月d日 EEEE", Locale.CHINA)

    val uiState: StateFlow<LogsUiState> = combine(
        eventRepository.observeAllEvents(),
        visibleMonth,
        selectedDate,
    ) { events, month, selectedDate ->
        LogsUiState(
            visibleMonth = month,
            monthTitle = "${month.year}年${month.monthValue}月",
            selectedDate = selectedDate,
            selectedDateLabel = selectedDate.format(dateLabelFormatter),
            monthDays = buildMonthDays(month, selectedDate),
            events = events.filter { it.date == selectedDate.toString() },
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LogsUiState(
            monthTitle = "${YearMonth.now().year}年${YearMonth.now().monthValue}月",
            selectedDateLabel = LocalDate.now().format(dateLabelFormatter),
            monthDays = buildMonthDays(YearMonth.now(), LocalDate.now()),
        )
    )

    fun showPreviousMonth() {
        showMonth(visibleMonth.value.minusMonths(1))
    }

    fun showNextMonth() {
        showMonth(visibleMonth.value.plusMonths(1))
    }

    fun selectDate(date: LocalDate) {
        selectedDate.value = date
        visibleMonth.value = YearMonth.from(date)
    }

    private fun showMonth(month: YearMonth) {
        visibleMonth.value = month
        if (YearMonth.from(selectedDate.value) != month) {
            selectedDate.value = if (month == YearMonth.now()) {
                LocalDate.now()
            } else {
                month.atDay(1)
            }
        }
    }

    private fun buildMonthDays(
        month: YearMonth,
        selectedDate: LocalDate,
    ): List<CalendarDayUi> {
        val today = LocalDate.now()
        val leadingEmptyDays = month.atDay(1).dayOfWeek.value - 1
        val days = mutableListOf<CalendarDayUi>()

        repeat(leadingEmptyDays) {
            days += CalendarDayUi(date = null, label = "")
        }

        for (day in 1..month.lengthOfMonth()) {
            val date = month.atDay(day)
            days += CalendarDayUi(
                date = date,
                label = day.toString(),
                isToday = date == today,
                isSelected = date == selectedDate,
            )
        }

        val trailingEmptyDays = (7 - days.size % 7) % 7
        repeat(trailingEmptyDays) {
            days += CalendarDayUi(date = null, label = "")
        }

        return days
    }
}
