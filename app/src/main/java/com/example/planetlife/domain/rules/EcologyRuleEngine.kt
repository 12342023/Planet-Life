package com.example.planetlife.domain.rules

import com.example.planetlife.data.local.entity.PlanetEntity
import kotlin.math.roundToInt

object EcologyRuleEngine {
    
    fun calculateNewValues(
        currentPlanet: PlanetEntity,
        walkingMinutes: Int = 0,
        sedentaryMinutes: Int = 0,
        nightActiveMinutes: Int = 0,
        commuteMinutes: Int = 0,
        focusMinutes: Int = 0 // For future focus logic, keeping it 0 for now
    ): PlanetEntity {
        var forest = currentPlanet.forestValue.toDouble()
        var crystal = currentPlanet.crystalValue.toDouble()
        var dream = currentPlanet.dreamValue.toDouble()
        var city = currentPlanet.cityValue.toDouble()
        var desert = currentPlanet.desertValue.toDouble()
        var shadow = currentPlanet.shadowValue.toDouble()

        // 1. Walking: forest +0.8, desert -0.2
        forest += walkingMinutes * 0.8
        desert -= walkingMinutes * 0.2

        // 2. Sedentary: desert + (hours * 8)
        val sedentaryHours = sedentaryMinutes / 60.0
        desert += sedentaryHours * 8

        // 3. Night Active: shadow +1.2, dream -0.5
        shadow += nightActiveMinutes * 1.2
        dream -= nightActiveMinutes * 0.5

        // 4. Commute: city +0.4
        city += commuteMinutes * 0.4
        
        // 5. Focus: crystal +1.0 (Phase 4, but good to have the hook)
        crystal += focusMinutes * 1.0

        return currentPlanet.copy(
            forestValue = forest.roundToInt().coerceIn(0, 100),
            crystalValue = crystal.roundToInt().coerceIn(0, 100),
            dreamValue = dream.roundToInt().coerceIn(0, 100),
            cityValue = city.roundToInt().coerceIn(0, 100),
            desertValue = desert.roundToInt().coerceIn(0, 100),
            shadowValue = shadow.roundToInt().coerceIn(0, 100),
            updatedAt = System.currentTimeMillis()
        )
    }
}
