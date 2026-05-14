package com.example.planetlife.domain.text

import com.example.planetlife.domain.model.EnergyType
import com.example.planetlife.domain.model.MoodWeather
import kotlin.random.Random

class LocalPlanetTextGenerator(
    private val random: Random = Random.Default,
) : PlanetTextGenerator {

    override fun generate(request: TextGenerationRequest): TextGenerationResult {
        return when (request.type) {
            TextGenerationType.ENERGY_FEEDBACK -> energyFeedback(request)
            TextGenerationType.DAILY_RESPONSE -> dailyResponse(request)
            TextGenerationType.MOOD_COMPANION -> moodCompanion(request)
            TextGenerationType.CREATURE_ENCOUNTER -> creatureEncounter(request)
            TextGenerationType.LEVEL_UP -> levelUp(request)
        }
    }

    private fun energyFeedback(request: TextGenerationRequest): TextGenerationResult {
        val energyType = request.energyType
        return TextGenerationResult(
            title = energyType?.title ?: "能量回应",
            body = energyType?.let { energyPhrases.getValue(it).pick() } ?: genericEnergyPhrases.pick(),
            tags = listOfNotNull("energy", energyType?.tag),
        )
    }

    private fun dailyResponse(request: TextGenerationRequest): TextGenerationResult {
        return TextGenerationResult(
            title = "今日星球回应",
            body = dailyResponsePhrases.pick().replace("{planetName}", request.planetName),
            tags = listOf("daily_response"),
        )
    }

    private fun moodCompanion(request: TextGenerationRequest): TextGenerationResult {
        val moodWeather = request.moodWeather ?: MoodWeather.BREEZE
        return TextGenerationResult(
            title = moodWeather.title,
            body = moodPhrases.getValue(moodWeather).pick(),
            tags = listOf("mood", moodWeather.tag),
        )
    }

    private fun creatureEncounter(request: TextGenerationRequest): TextGenerationResult {
        val creatureName = request.creatureName ?: "小小访客"
        return TextGenerationResult(
            title = "遇见$creatureName",
            body = creaturePhrases.pick().replace("{creatureName}", creatureName),
            tags = listOf("creature_met"),
        )
    }

    private fun levelUp(request: TextGenerationRequest): TextGenerationResult {
        val levelText = request.planetLevel?.let { "第 $it 级" } ?: "新的阶段"
        return TextGenerationResult(
            title = "星球长大了",
            body = levelUpPhrases.pick().replace("{levelText}", levelText),
            tags = listOf("level_up"),
        )
    }

    private fun List<String>.pick(): String = this[random.nextInt(size)]

    private companion object {
        val energyPhrases = mapOf(
            EnergyType.OCEAN to listOf(
                "潮汐轻轻推上岸边，星球喝到了一点清凉。",
                "一滴水落进星球的海，慢慢变成一圈发光的涟漪。",
            ),
            EnergyType.SOIL to listOf(
                "一点温热的养分落进星壤，星球慢慢安定下来。",
                "地底亮起一点温暖的光，生命力正在慢慢往上走。",
            ),
            EnergyType.FOREST to listOf(
                "风从星球南部经过，几片新的绿意悄悄展开。",
                "森林边缘轻轻晃了一下，像是在回应你的脚步。",
            ),
            EnergyType.DREAM to listOf(
                "梦境能量落进夜色里，星球把柔软慢慢收好。",
                "一小片安静的云停下来，替星球守住今天的休息。",
            ),
            EnergyType.LIGHT to listOf(
                "一束柔光越过云层，星球把今天照得更暖了一点。",
                "光照轻轻落下，远处的山脊亮起很浅的金色。",
            ),
            EnergyType.STAR to listOf(
                "你回来了一下，星球的夜空就多了一点亮。",
                "一颗小星在轨道上亮起，像在记得你今天来过。",
            ),
            EnergyType.CORE to listOf(
                "星核安静地亮了一下，把专注的时间收进深处。",
                "星球的心口多了一点稳定的光，慢慢托住今天。",
            ),
        )

        val moodPhrases = mapOf(
            MoodWeather.SUNNY to listOf(
                "今天的天空很轻，星球把这点明亮好好接住了。",
            ),
            MoodWeather.BREEZE to listOf(
                "有一点风经过也很好，星球陪你慢慢待在这里。",
            ),
            MoodWeather.CLOUDY to listOf(
                "云多一点也没关系，星球把光放低，陪你安静一会儿。",
            ),
            MoodWeather.RAIN to listOf(
                "你不用急着放晴，星球让细小的光雨陪你慢慢待着。",
            ),
            MoodWeather.THUNDER to listOf(
                "今天的天气有些响，星球把夜色铺厚一点，先陪你稳住。",
            ),
        )

        val dailyResponsePhrases = listOf(
            "{planetName}今天安静地待在这里，把你愿意回来看它的这一刻认真收好。",
            "今天的{planetName}没有催促什么，只是轻轻亮着，等你把一点照顾放在这里。",
        )

        val genericEnergyPhrases = listOf(
            "一点轻轻的能量抵达星球，被它稳稳地收进身体里。",
            "星球感到有一点光靠近，安静地把这份照顾留下。",
        )

        val creaturePhrases = listOf(
            "你遇见了{creatureName}。它在低云旁停下脚步，轻轻看了看今天的星球。",
            "{creatureName}从草叶后探出头，把一小段温柔的动静留在这里。",
        )

        val levelUpPhrases = listOf(
            "星球抵达了{levelText}，它把这些天收到的照顾都悄悄收进身体里。",
            "新的星光慢慢展开，星球来到{levelText}，看起来比昨天更有力气。",
        )

        val EnergyType.title: String
            get() = when (this) {
                EnergyType.OCEAN -> "海洋能量"
                EnergyType.SOIL -> "土壤能量"
                EnergyType.FOREST -> "森林能量"
                EnergyType.DREAM -> "梦境能量"
                EnergyType.LIGHT -> "光照能量"
                EnergyType.STAR -> "星辰能量"
                EnergyType.CORE -> "星核能量"
            }

        val EnergyType.tag: String
            get() = name.lowercase()

        val MoodWeather.title: String
            get() = when (this) {
                MoodWeather.SUNNY -> "晴天心情"
                MoodWeather.BREEZE -> "微风心情"
                MoodWeather.CLOUDY -> "多云心情"
                MoodWeather.RAIN -> "小雨心情"
                MoodWeather.THUNDER -> "雷雨心情"
            }

        val MoodWeather.tag: String
            get() = name.lowercase()
    }
}
