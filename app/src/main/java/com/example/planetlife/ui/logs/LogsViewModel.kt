package com.example.planetlife.ui.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planetlife.data.local.entity.PlanetEventEntity
import com.example.planetlife.data.repository.PlanetEventRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class LogsUiState(
    val events: List<PlanetEventEntity> = emptyList(),
    val isLoading: Boolean = true
)

class LogsViewModel(
    private val eventRepository: PlanetEventRepository
) : ViewModel() {

    val uiState: StateFlow<LogsUiState> = eventRepository.observeAllEvents()
        .map { events ->
            LogsUiState(events = events, isLoading = false)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LogsUiState()
        )
}
