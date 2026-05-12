package com.example.planetlife.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.planetlife.data.local.entity.PlanetTaskEntity
import com.example.planetlife.ui.components.CreamPanel
import com.example.planetlife.ui.components.PageContent
import com.example.planetlife.ui.theme.*

@Composable
fun TasksScreen(viewModel: TasksViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    PageContent(
        title = "生态任务",
        subtitle = "完成日常行动，为星球注入能量"
    ) {
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TitleBlue)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(uiState.tasks) { task ->
                    TaskCard(
                        task = task,
                        onClaimClick = { viewModel.claimReward(task) }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    task: PlanetTaskEntity,
    onClaimClick: () -> Unit
) {
    val progress = (task.currentValue.toFloat() / task.targetValue.toFloat()).coerceIn(0f, 1f)
    
    val statusText: String
    val statusColor: Color
    when {
        task.claimed -> {
            statusText = "已领取"
            statusColor = TextBrown.copy(alpha = 0.6f)
        }
        task.completed -> {
            statusText = "已完成"
            statusColor = ForestGreen
        }
        task.taskType == "SEDENTARY_LIMIT" || task.taskType == "NIGHT_ACTIVE_LIMIT" -> {
            statusText = "守护中"
            statusColor = DreamPurple
        }
        else -> {
            statusText = "进行中"
            statusColor = TitleBlue
        }
    }

    CreamPanel {
        Column(modifier = Modifier.padding(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TitleBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = statusText,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextBrown
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 进度条区域
            Column {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .background(CreamBorder, RoundedCornerShape(5.dp)),
                    color = if (task.completed) ForestGreen else TitleBlue,
                    trackColor = Color.Transparent,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "${task.currentValue} / ${task.targetValue}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextBrown.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 奖励和操作
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val rewardLabel = when (task.rewardType) {
                    "FOREST" -> "森林能量 +${task.rewardValue}"
                    "CRYSTAL" -> "水晶能量 +${task.rewardValue}"
                    "DESERT_REDUCE" -> "荒漠净化 +${task.rewardValue}"
                    "DREAM" -> "梦境能量 +${task.rewardValue}"
                    else -> "未知奖励"
                }

                Text(
                    text = "奖励：$rewardLabel",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (task.claimed) TextBrown.copy(alpha = 0.5f) else DesertOrange
                )

                Button(
                    onClick = onClaimClick,
                    enabled = task.completed && !task.claimed,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForestGreen,
                        contentColor = Color.White,
                        disabledContainerColor = CreamBorder,
                        disabledContentColor = TextBrown.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                ) {
                    Text(if (task.claimed) "已领取" else "领取奖励", fontSize = 14.sp)
                }
            }
        }
    }
}
