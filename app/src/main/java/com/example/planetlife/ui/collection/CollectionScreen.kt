package com.example.planetlife.ui.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.planetlife.data.local.entity.CreatureEntity
import com.example.planetlife.ui.components.CreamPanel
import com.example.planetlife.ui.components.MiniPlanetIcon
import com.example.planetlife.ui.components.SkyBackground
import com.example.planetlife.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CollectionScreen(
    viewModel: CollectionViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    SkyBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CollectionTitlePanel(onBack = onBack)
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TitleBlue)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.creatures) { creature ->
                        CreatureCard(creature)
                    }
                }
            }
        }
    }
}

@Composable
private fun CollectionTitlePanel(onBack: () -> Unit) {
    CreamPanel {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Text(
                    text = "‹",
                    color = TitleBlue,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            MiniPlanetIcon()
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "星球图鉴", style = MaterialTheme.typography.headlineLarge)
                Text(text = "探索星球上奇妙的生命与景观", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun CreatureCard(creature: CreatureEntity) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    CreamPanel(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon Placeholder
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        if (creature.isUnlocked) ForestGreen.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (creature.isUnlocked) getEmojiForCreature(creature.name) else "🔒",
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (creature.isUnlocked) creature.name else "???",
                style = MaterialTheme.typography.titleMedium,
                color = if (creature.isUnlocked) TitleBlue else TextBrown.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            if (creature.isUnlocked) {
                Surface(
                    color = getRarityColor(creature.rarity).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = creature.rarity,
                        color = getRarityColor(creature.rarity),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Text(
                    text = creature.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextBrown,
                    maxLines = 3,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(4.dp))
                
                creature.unlockedAt?.let {
                    Text(
                        text = "解锁于 ${dateFormat.format(Date(it))}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextBrown.copy(alpha = 0.6f),
                        fontSize = 9.sp
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "解锁条件：",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextBrown.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = creature.unlockCondition,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextBrown.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun getEmojiForCreature(name: String): String {
    return when (name) {
        "森林鹿" -> "🦌"
        "水晶猫" -> "🐱"
        "睡眠水母" -> "🪼"
        "沙漠机械虫" -> "🐛"
        "夜行蘑菇人" -> "🍄"
        else -> "❓"
    }
}

private fun getRarityColor(rarity: String): Color {
    return when (rarity) {
        "稀有" -> DreamPurple
        "传奇" -> DesertOrange
        else -> ForestGreen
    }
}
