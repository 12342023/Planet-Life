package com.example.planetlife.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.planetlife.ui.components.CreamPanel
import com.example.planetlife.ui.components.PageContent
import com.example.planetlife.ui.theme.*

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onComplete: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    PageContent(
        title = "口袋星球",
        subtitle = "你的生活，正在形成一颗星球"
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CreamPanel {
                Text(
                    text = "星球命名",
                    style = MaterialTheme.typography.titleMedium,
                    color = TitleBlue
                )
                OutlinedTextField(
                    value = uiState.planetName,
                    onValueChange = { viewModel.onPlanetNameChange(it) },
                    placeholder = { Text("给你的小星球起个名字...") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.error != null,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = CreamBorder
                    )
                )
                if (uiState.error != null) {
                    Text(
                        text = uiState.error!!,
                        color = WarningRed,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            CreamPanel {
                Text(
                    text = "你的昵称",
                    style = MaterialTheme.typography.titleMedium,
                    color = TitleBlue
                )
                OutlinedTextField(
                    value = uiState.nickname,
                    onValueChange = { viewModel.onNicknameChange(it) },
                    placeholder = { Text("星际旅人 (默认)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = CreamBorder
                    )
                )
            }

            CreamPanel {
                Text(
                    text = "初始风格",
                    style = MaterialTheme.typography.titleMedium,
                    color = TitleBlue
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val styles = listOf("默认", "森林", "梦境")
                    styles.forEach { style ->
                        StyleChip(
                            label = style,
                            selected = uiState.selectedStyle == style,
                            onClick = { viewModel.onStyleSelect(style) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.createPlanet(onComplete) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ForestGreen,
                    contentColor = Color.White
                ),
                enabled = uiState.planetName.isNotBlank() && !uiState.isCreating
            ) {
                if (uiState.isCreating) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("开启我的星球旅程", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun StyleChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) ForestGreen else CreamLight
    val contentColor = if (selected) Color.White else TextBrown
    val borderColor = if (selected) ForestGreen else CreamBorder

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = contentColor, fontWeight = FontWeight.Bold)
    }
}
