package com.example.planetlife.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.planetlife.data.local.entity.PlanetEventEntity
import com.example.planetlife.ui.components.CreamPanel
import com.example.planetlife.ui.components.DynamicPlanet
import com.example.planetlife.ui.components.EcologyMeter
import com.example.planetlife.ui.components.PageContent
import com.example.planetlife.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val planet = uiState.planet
    val settings = uiState.settings
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        PageContent(
            title = uiState.appName,
            subtitle = if (planet != null) {
                "${planet.name} · Lv.${planet.level} · ${uiState.planetStatus}"
            } else {
                "未命名星球 · Lv.1 · 生态稳定"
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (planet != null) {
                        DynamicPlanet(
                            forest = planet.forestValue,
                            crystal = planet.crystalValue,
                            dream = planet.dreamValue,
                            city = planet.cityValue,
                            desert = planet.desertValue,
                            shadow = planet.shadowValue,
                        )
                    } else {
                        DynamicPlanet(52, 38, 46, 24, 18, 12)
                    }
                }

                CreamPanel {
                    Text(
                        text = "生态概览",
                        style = MaterialTheme.typography.titleLarge,
                        color = TitleBlue,
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        val ecologyData = if (planet != null) {
                            listOf(
                                Triple("森林", planet.forestValue, ForestGreen),
                                Triple("水晶", planet.crystalValue, CrystalBlue),
                                Triple("梦境", planet.dreamValue, DreamPurple),
                                Triple("城市", planet.cityValue, CityGold),
                                Triple("荒漠", planet.desertValue, DesertOrange),
                                Triple("暗影", planet.shadowValue, ShadowPurple),
                            )
                        } else {
                            emptyList()
                        }

                        ecologyData.forEach { (label, value, color) ->
                            Column(modifier = Modifier.sizeIn(minWidth = 132.dp)) {
                                EcologyMeter(label = label, value = value, color = color)
                            }
                        }
                    }
                }

                TodayEventsPanel(events = uiState.todayEvents, nickname = settings.nickname)

                FeedingPanel(
                    onOceanEnergy = {
                        viewModel.recordOceanEnergy { message ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                    },
                    onComingSoon = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("下一步接入")
                        }
                    },
                )

                BehaviorRecordPanel(
                    onRecord = { w, s, n, c ->
                        viewModel.recordBehavior(w, s, n, c) { message ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                    },
                    onRandomizeEcology = {
                        viewModel.randomizeEcologyForDemo { message ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                    },
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
        )
    }
}

@Composable
fun FeedingPanel(
    onOceanEnergy: () -> Unit,
    onComingSoon: () -> Unit,
) {
    CreamPanel {
        Text(
            text = "今日轻喂养",
            style = MaterialTheme.typography.titleLarge,
            color = TitleBlue,
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FeedingButton(text = "补充一杯海洋能量", color = CrystalBlue, onClick = onOceanEnergy)
            FeedingButton(text = "种下一点土壤能量", color = CityGold, onClick = onComingSoon)
            FeedingButton(text = "送来一点森林能量", color = ForestGreen, onClick = onComingSoon)
            FeedingButton(text = "记录今日天气", color = DreamPurple, onClick = onComingSoon)
        }
    }
}

@Composable
private fun FeedingButton(text: String, color: Color, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color),
        shape = MaterialTheme.shapes.medium,
    ) {
        Text(text = text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TodayEventsPanel(events: List<PlanetEventEntity>, nickname: String) {
    CreamPanel {
        Text(
            text = "今日事件",
            style = MaterialTheme.typography.titleLarge,
            color = TitleBlue,
        )
        if (events.isEmpty()) {
            Text(
                text = "欢迎回来，${nickname}。星球目前很安静，正在发出稳定的微光。",
                style = MaterialTheme.typography.bodyLarge,
                color = TextBrown,
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                events.forEach { event ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (event.rarity == "警告") WarningRed else ForestGreen)
                        )
                        Column {
                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = TitleBlue,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = event.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextBrown
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BehaviorRecordPanel(
    onRecord: (Int, Int, Int, Int) -> Unit,
    onRandomizeEcology: () -> Unit,
) {
    var walking by remember { mutableStateOf("") }
    var sedentary by remember { mutableStateOf("") }
    var nightActive by remember { mutableStateOf("") }
    var commute by remember { mutableStateOf("") }

    CreamPanel {
        Text(
            text = "手动记录行为",
            style = MaterialTheme.typography.titleLarge,
            color = TitleBlue,
        )
        Text(
            text = "记录步行、专注外的日常行为，星球会立刻长出森林、城市或荒漠变化。",
            style = MaterialTheme.typography.bodyMedium,
            color = TextBrown,
        )
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            BehaviorInputField(label = "步行 (分钟)", value = walking, onValueChange = { walking = it })
            BehaviorInputField(label = "久坐 (分钟)", value = sedentary, onValueChange = { sedentary = it })
            BehaviorInputField(label = "夜间活跃 (分钟)", value = nightActive, onValueChange = { nightActive = it })
            BehaviorInputField(label = "通勤/外出 (分钟)", value = commute, onValueChange = { commute = it })
        }

        Button(
            onClick = {
                onRecord(
                    walking.toIntOrNull()?.coerceAtLeast(0) ?: 0,
                    sedentary.toIntOrNull()?.coerceAtLeast(0) ?: 0,
                    nightActive.toIntOrNull()?.coerceAtLeast(0) ?: 0,
                    commute.toIntOrNull()?.coerceAtLeast(0) ?: 0
                )
                walking = ""
                sedentary = ""
                nightActive = ""
                commute = ""
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("同步至星球", color = Color.White, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
            onClick = onRandomizeEcology,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TitleBlue),
            shape = MaterialTheme.shapes.medium,
        ) {
            Text("随机生态演示", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BehaviorInputField(label: String, value: String, onValueChange: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextBrown, fontSize = 14.sp)
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it.filter(Char::isDigit)) },
            modifier = Modifier.width(100.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ForestGreen,
                unfocusedBorderColor = CreamBorder
            )
        )
    }
}
