package com.example.planetlife.ui.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planetlife.data.local.entity.PlanetEventEntity
import com.example.planetlife.data.repository.PlanetEventRepository
import com.example.planetlife.domain.model.EnergyType
import com.example.planetlife.domain.model.PlanetLogType
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
    val markers: List<CalendarMarkerType> = emptyList(),
)

enum class CalendarMarkerType {
    OCEAN,
    SOIL,
    FOREST,
    DREAM,
    LIGHT,
    STAR,
    CORE,
    MOOD,
    CREATURE,
}

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
        val markersByDate = buildMarkersByDate(events)
        LogsUiState(
            visibleMonth = month,
            monthTitle = "${month.year}年${month.monthValue}月",
            selectedDate = selectedDate,
            selectedDateLabel = selectedDate.format(dateLabelFormatter),
            monthDays = buildMonthDays(month, selectedDate, markersByDate),
            events = events.filter { it.date == selectedDate.toString() },
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LogsUiState(
            monthTitle = "${YearMonth.now().year}年${YearMonth.now().monthValue}月",
            selectedDateLabel = LocalDate.now().format(dateLabelFormatter),
            monthDays = buildMonthDays(YearMonth.now(), LocalDate.now(), emptyMap()),
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
        markersByDate: Map<String, List<CalendarMarkerType>>,
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
                markers = markersByDate[date.toString()].orEmpty(),
            )
        }

        val trailingEmptyDays = (7 - days.size % 7) % 7
        repeat(trailingEmptyDays) {
            days += CalendarDayUi(date = null, label = "")
        }

        return days
    }

    private fun buildMarkersByDate(events: List<PlanetEventEntity>): Map<String, List<CalendarMarkerType>> {
        return events.groupBy { it.date }.mapValues { (_, dayEvents) ->
            dayEvents
                .flatMap { it.calendarMarkers() }
                .distinct()
                .take(3)
        }
    }

    private fun PlanetEventEntity.calendarMarkers(): List<CalendarMarkerType> {
        val markers = mutableListOf<CalendarMarkerType>()

        when (energyType) {
            EnergyType.OCEAN.name -> markers += CalendarMarkerType.OCEAN
            EnergyType.SOIL.name -> markers += CalendarMarkerType.SOIL
            EnergyType.FOREST.name -> markers += CalendarMarkerType.FOREST
            EnergyType.DREAM.name -> markers += CalendarMarkerType.DREAM
            EnergyType.LIGHT.name -> markers += CalendarMarkerType.LIGHT
            EnergyType.STAR.name -> markers += CalendarMarkerType.STAR
            EnergyType.CORE.name -> markers += CalendarMarkerType.CORE
        }

        val type = logType ?: eventType
        if (moodWeather != null || type == PlanetLogType.MOOD.name) {
            markers += CalendarMarkerType.MOOD
        }
        if (type == PlanetLogType.CREATURE_APPEARED.name || type == PlanetLogType.CREATURE_MET.name) {
            markers += CalendarMarkerType.CREATURE
        }

        return markers
    }
}
