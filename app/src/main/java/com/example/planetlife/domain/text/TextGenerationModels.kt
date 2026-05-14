package com.example.planetlife.domain.text

import com.example.planetlife.domain.model.EnergyType
import com.example.planetlife.domain.model.MoodWeather

enum class TextGenerationType {
    ENERGY_FEEDBACK,
    DAILY_RESPONSE,
    MOOD_COMPANION,
    CREATURE_ENCOUNTER,
    LEVEL_UP,
}

data class TextGenerationRequest(
    val type: TextGenerationType,
    val planetName: String = "星球",
    val energyType: EnergyType? = null,
    val moodWeather: MoodWeather? = null,
    val creatureName: String? = null,
    val planetLevel: Int? = null,
)

data class TextGenerationResult(
    val title: String,
    val body: String,
    val tags: List<String> = emptyList(),
    val tone: String = "gentle",
    val safetyLevel: String = "safe",
)
