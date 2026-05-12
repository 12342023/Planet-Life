package com.example.planetlife.ui.logs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.planetlife.data.local.entity.PlanetEventEntity
import com.example.planetlife.ui.components.CreamPanel
import com.example.planetlife.ui.components.PageContent
import com.example.planetlife.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LogsScreen(viewModel: LogsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    PageContent(title = "星球日志", subtitle = "记录每一颗微小的生态波动") {
        if (uiState.events.isEmpty()) {
            EmptyLogsView()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(uiState.events) { index, event ->
                    TimelineEventItem(
                        event = event,
                        time = timeFormat.format(Date(event.createdAt)),
                        isLast = index == uiState.events.size - 1
                    )
                }
            }
        }
    }
}

@Composable
fun TimelineEventItem(event: PlanetEventEntity, time: String, isLast: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Timeline indicator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            Text(
                text = time,
                style = MaterialTheme.typography.labelMedium,
                color = TitleBlue,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (event.rarity == "警告") WarningRed else ForestGreen)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(CreamBorder)
                )
            }
        }

        // Event Card
        CreamPanel(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TitleBlue,
                    fontWeight = FontWeight.Bold
                )
                if (event.rarity != "普通") {
                    Badge(text = event.rarity, color = if (event.rarity == "警告") WarningRed else CityGold)
                }
            }
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextBrown
            )
            Text(
                text = "关联领域: ${event.relatedValue}",
                style = MaterialTheme.typography.labelSmall,
                color = TitleBlue.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun Badge(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            fontSize = 10.sp,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EmptyLogsView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "星球目前很安静，期待你的下一次探索。",
            style = MaterialTheme.typography.bodyLarge,
            color = TextBrown.copy(alpha = 0.6f)
        )
    }
}
