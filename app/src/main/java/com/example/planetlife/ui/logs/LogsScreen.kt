package com.example.planetlife.ui.logs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.planetlife.data.local.entity.PlanetEventEntity
import com.example.planetlife.ui.components.CreamPanel
import com.example.planetlife.ui.components.PageContent
import com.example.planetlife.ui.theme.*
import java.time.LocalDate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LogsScreen(viewModel: LogsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    PageContent(title = "星历", subtitle = "星球和你一起生活过的日期") {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                CalendarPanel(
                    monthTitle = uiState.monthTitle,
                    days = uiState.monthDays,
                    onPreviousMonth = viewModel::showPreviousMonth,
                    onNextMonth = viewModel::showNextMonth,
                    onSelectDate = viewModel::selectDate,
                )
            }
            item {
                SelectedDateHeader(selectedDateLabel = uiState.selectedDateLabel)
            }
            if (uiState.events.isEmpty()) {
                item {
                    EmptyLogsView()
                }
            } else {
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
private fun CalendarPanel(
    monthTitle: String,
    days: List<CalendarDayUi>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSelectDate: (LocalDate) -> Unit,
) {
    CreamPanel {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onPreviousMonth) {
                Text("上个月", color = TitleBlue)
            }
            Text(
                text = monthTitle,
                style = MaterialTheme.typography.titleLarge,
                color = TitleBlue,
                fontWeight = FontWeight.Bold,
            )
            TextButton(onClick = onNextMonth) {
                Text("下个月", color = TitleBlue)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        CalendarWeekHeader()
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            days.chunked(7).forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    week.forEach { day ->
                        CalendarDayCell(day = day, onSelectDate = onSelectDate)
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarWeekHeader() {
    val weekdays = listOf("一", "二", "三", "四", "五", "六", "日")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        weekdays.forEach { weekday ->
            Text(
                text = weekday,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelMedium,
                color = TitleBlue.copy(alpha = 0.72f),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }
    Spacer(modifier = Modifier.height(6.dp))
}

@Composable
private fun RowScope.CalendarDayCell(
    day: CalendarDayUi,
    onSelectDate: (LocalDate) -> Unit,
) {
    val date = day.date
    if (date == null) {
        Spacer(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
        )
        return
    }

    val shape = RoundedCornerShape(8.dp)
    val background = when {
        day.isSelected -> TitleBlue
        day.isToday -> CityGold.copy(alpha = 0.18f)
        else -> Color.Transparent
    }
    val borderColor = when {
        day.isSelected -> TitleBlue
        day.isToday -> CityGold
        else -> CreamBorder.copy(alpha = 0.55f)
    }
    val textColor = if (day.isSelected) Color.White else TextBrown

    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .clip(shape)
            .background(background)
            .border(1.dp, borderColor, shape)
            .clickable { onSelectDate(date) },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = day.label,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            fontWeight = if (day.isToday || day.isSelected) FontWeight.Bold else FontWeight.Normal,
        )
    }
}

@Composable
private fun SelectedDateHeader(selectedDateLabel: String) {
    Text(
        text = selectedDateLabel,
        style = MaterialTheme.typography.titleMedium,
        color = TitleBlue,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 4.dp),
    )
}

@Composable
private fun TimelineEventItem(event: PlanetEventEntity, time: String, isLast: Boolean) {
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
private fun Badge(text: String, color: Color) {
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
private fun EmptyLogsView() {
    CreamPanel {
        Text(
            text = "这一天的星球很安静，什么也没有丢失。\n空白也是生活的一部分。",
            style = MaterialTheme.typography.bodyLarge,
            color = TextBrown.copy(alpha = 0.72f)
        )
    }
}
