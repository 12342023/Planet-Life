package com.example.planetlife.ui.focus

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.planetlife.ui.components.CreamPanel
import com.example.planetlife.ui.components.PageContent
import com.example.planetlife.ui.theme.*

@Composable
fun FocusScreen(viewModel: FocusViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    PageContent(title = "专注模式", subtitle = uiState.statusText) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TimerPanel(
                displayTime = uiState.displayTime,
                progress = if (uiState.selectedMinutes > 0) uiState.remainingSeconds.toFloat() / (uiState.selectedMinutes * 60) else 0f,
                status = uiState.status,
                onStart = viewModel::startTimer,
                onPause = viewModel::pauseTimer,
                onResume = viewModel::resumeTimer,
                onStop = viewModel::stopTimer
            )

            if (uiState.status == TimerStatus.IDLE) {
                DurationSelectionPanel(
                    selectedMinutes = uiState.selectedMinutes,
                    onSelect = viewModel::selectMinutes
                )
            } else {
                FocusFeedbackPanel(
                    showFeedback = uiState.showFeedback,
                    reward = uiState.lastReward
                )
            }
        }
    }
}

@Composable
fun TimerPanel(
    displayTime: String,
    progress: Float,
    status: TimerStatus,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit
) {
    CreamPanel {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.size(220.dp)) {
                // Background circle
                drawCircle(color = CreamBorder, style = Stroke(width = 12.dp.toPx()))
                
                // Progress arc
                drawArc(
                    color = CrystalBlue,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round),
                )
                
                // Simple Crystal Tower / Island Visual
                drawCircle(
                    color = CrystalBlue.copy(alpha = 0.2f),
                    radius = 60.dp.toPx(),
                    center = Offset(size.width / 2f, size.height / 2f)
                )
            }
            Text(
                text = displayTime,
                style = MaterialTheme.typography.displayMedium,
                color = TitleBlue,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (status) {
                TimerStatus.IDLE, TimerStatus.FINISHED -> {
                    Button(
                        onClick = onStart,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("开始专注", fontWeight = FontWeight.Bold)
                    }
                }
                TimerStatus.RUNNING -> {
                    Button(
                        onClick = onPause,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = CityGold),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("暂停", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onStop,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WarningRed)
                    ) {
                        Text("结束")
                    }
                }
                TimerStatus.PAUSED -> {
                    Button(
                        onClick = onResume,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("继续", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onStop,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WarningRed)
                    ) {
                        Text("结束")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DurationSelectionPanel(selectedMinutes: Int, onSelect: (Int) -> Unit) {
    var customText by remember { mutableStateOf("") }

    CreamPanel {
        Text(text = "专注时长", style = MaterialTheme.typography.titleLarge, color = TitleBlue)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(25, 45).forEach { mins ->
                FilterChip(
                    selected = selectedMinutes == mins,
                    onClick = { onSelect(mins) },
                    label = { Text("${mins}分钟") }
                )
            }
        }
        
        OutlinedTextField(
            value = customText,
            onValueChange = { 
                customText = it.filter { char -> char.isDigit() }
                val mins = customText.toIntOrNull()
                if (mins != null && mins > 0) onSelect(mins)
            },
            label = { Text("自定义分钟") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CrystalBlue,
                unfocusedBorderColor = CreamBorder
            )
        )
    }
}

@Composable
fun FocusFeedbackPanel(showFeedback: Boolean, reward: Int) {
    if (showFeedback) {
        CreamPanel {
            Text(text = "专注成果", style = MaterialTheme.typography.titleLarge, color = TitleBlue)
            Text(
                text = "你完成了一次深度专注，水晶塔吸收了能量。",
                style = MaterialTheme.typography.bodyLarge,
                color = TextBrown
            )
            Text(
                text = "+$reward 水晶能量",
                style = MaterialTheme.typography.headlineSmall,
                color = CrystalBlue,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
