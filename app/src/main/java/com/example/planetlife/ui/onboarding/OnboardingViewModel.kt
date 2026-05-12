package com.example.planetlife.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planetlife.data.local.entity.PlanetEntity
import com.example.planetlife.data.repository.PlanetRepository
import com.example.planetlife.data.settings.SettingsDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val planetName: String = "",
    val nickname: String = "",
    val selectedStyle: String = "默认",
    val isCreating: Boolean = false,
    val error: String? = null
)

class OnboardingViewModel(
    private val planetRepository: PlanetRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onPlanetNameChange(name: String) {
        _uiState.update { it.copy(planetName = name, error = null) }
    }

    fun onNicknameChange(name: String) {
        _uiState.update { it.copy(nickname = name) }
    }

    fun onStyleSelect(style: String) {
        _uiState.update { it.copy(selectedStyle = style) }
    }

    fun createPlanet(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        if (currentState.planetName.isBlank()) {
            _uiState.update { it.copy(error = "星球名称不能为空") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isCreating = true) }
            
            // Save Planet
            val newPlanet = PlanetEntity(
                name = currentState.planetName,
                currentTheme = currentState.selectedStyle
            )
            planetRepository.savePlanet(newPlanet)
            
            // Save User Settings
            settingsDataStore.updateNickname(currentState.nickname)
            
            onSuccess()
        }
    }
}
