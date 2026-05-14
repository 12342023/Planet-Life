package com.example.planetlife.domain.text

interface PlanetTextGenerator {
    fun generate(request: TextGenerationRequest): TextGenerationResult
}
