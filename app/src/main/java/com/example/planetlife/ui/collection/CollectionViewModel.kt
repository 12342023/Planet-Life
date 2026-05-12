package com.example.planetlife.ui.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planetlife.data.local.entity.CreatureEntity
import com.example.planetlife.data.repository.CollectionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CollectionUiState(
    val creatures: List<CreatureEntity> = emptyList(),
    val isLoading: Boolean = true
)

class CollectionViewModel(
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    val uiState: StateFlow<CollectionUiState> = collectionRepository.getAllCreatures()
        .map { creatures ->
            CollectionUiState(creatures = creatures, isLoading = false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CollectionUiState()
        )

    init {
        viewModelScope.launch {
            collectionRepository.ensureDefaultCreatures()
        }
    }
}
