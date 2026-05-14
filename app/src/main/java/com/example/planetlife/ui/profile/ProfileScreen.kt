package com.example.planetlife.ui.profile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.planetlife.data.settings.UserSettings
import com.example.planetlife.ui.components.CreamPanel
import com.example.planetlife.ui.components.PageContent
import com.example.planetlife.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToCollection: () -> Unit,
    onDataCleared: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    PageContent(title = "我的", subtitle = "个人成就与系统设置") {
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TitleBlue)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // 用户资料大卡片
                item {
                    ProfileHeaderCard(
                        nickname = uiState.settings.nickname,
                        planetName = uiState.planet?.name ?: "未知星球",
                        level = uiState.planet?.level ?: 1,
                        unlockedCount = uiState.collectionUnlockedCount,
                        totalCount = uiState.collectionTotalCount,
                        onEditClick = { showEditDialog = true }
                    )
                }

                // 累计统计
                item {
                    StatsSection(
                        totalFocus = uiState.totalFocusMinutes,
                        totalWalking = uiState.totalWalkingMinutes
                    )
                }

                // 功能入口
                item {
                    EntryCard(
                        title = "星球图鉴",
                        subtitle = "查看已解锁的生物与奇观 (${uiState.collectionUnlockedCount}/${uiState.collectionTotalCount})",
                        icon = "📖",
                        onClick = onNavigateToCollection
                    )
                }

                // 系统设置
                item {
                    SettingsSection(
                        settings = uiState.settings,
                        onUpdateTheme = viewModel::updateThemeMode,
                        onUpdateWalkingGoal = viewModel::updateWalkingGoal,
                        onUpdateFocusGoal = viewModel::updateFocusGoal,
                        onUpdateSedentary = viewModel::updateSedentaryReminder,
                        onUpdateNotification = viewModel::updateNotificationEnabled
                    )
                }

                // 其他
                item {
                    CreamPanel {
                        Text(
                            text = "更多",
                            style = MaterialTheme.typography.titleSmall,
                            color = TitleBlue,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showPrivacyDialog = true }
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "隐私说明", style = MaterialTheme.typography.bodyLarge, color = TextBrown)
                            Text(text = "〉", color = TextBrown.copy(alpha = 0.5f))
                        }

                        HorizontalDivider(color = CreamBorder.copy(alpha = 0.5f))

                        Button(
                            onClick = { showClearConfirmDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFD96A55),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("清空本地数据", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // --- Dialogs ---

    if (showEditDialog) {
        EditProfileDialog(
            currentNickname = uiState.settings.nickname,
            currentPlanetName = uiState.planet?.name ?: "",
            onDismiss = { showEditDialog = false },
            onConfirm = { nickname, planetName ->
                viewModel.updateNickname(nickname)
                viewModel.updatePlanetName(planetName)
                showEditDialog = false
            }
        )
    }

    if (showPrivacyDialog) {
        PrivacyDialog(onDismiss = { showPrivacyDialog = false })
    }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("清空所有数据？", color = TitleBlue, fontWeight = FontWeight.Bold) },
            text = { Text("这将永久删除你的星球、日志、任务进度和所有设置。此操作不可撤销。", color = TextBrown) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllData {
                            showClearConfirmDialog = false
                            onDataCleared()
                        }
                    }
                ) {
                    Text("确定清空", color = Color(0xFFD96A55), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("取消", color = TextBrown)
                }
            },
            containerColor = Color(0xFFFFFDF4),
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun ProfileHeaderCard(
    nickname: String,
    planetName: String,
    level: Int,
    unlockedCount: Int,
    totalCount: Int,
    onEditClick: () -> Unit
) {
    CreamPanel {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nickname,
                    style = MaterialTheme.typography.headlineSmall,
                    color = TitleBlue,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$planetName · Lv.$level",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextBrown
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = ForestGreen.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "图鉴进度: $unlockedCount / $totalCount",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = ForestGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "编辑", tint = TitleBlue)
            }
        }
    }
}

@Composable
fun StatsSection(totalFocus: Int, totalWalking: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CreamPanel(modifier = Modifier.weight(1f)) {
            Text(text = "累计专注", style = MaterialTheme.typography.labelMedium, color = TextBrown)
            Text(
                text = "$totalFocus",
                style = MaterialTheme.typography.headlineSmall,
                color = TitleBlue,
                fontWeight = FontWeight.Bold
            )
            Text(text = "分钟", style = MaterialTheme.typography.labelSmall, color = TextBrown.copy(alpha = 0.6f))
        }
        CreamPanel(modifier = Modifier.weight(1f)) {
            Text(text = "累计步行", style = MaterialTheme.typography.labelMedium, color = TextBrown)
            Text(
                text = "$totalWalking",
                style = MaterialTheme.typography.headlineSmall,
                color = ForestGreen,
                fontWeight = FontWeight.Bold
            )
            Text(text = "分钟", style = MaterialTheme.typography.labelSmall, color = TextBrown.copy(alpha = 0.6f))
        }
    }
}

@Composable
fun EntryCard(title: String, subtitle: String, icon: String, onClick: () -> Unit) {
    CreamPanel(modifier = Modifier.clickable { onClick() }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium, color = TitleBlue, fontWeight = FontWeight.Bold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextBrown)
            }
            Text(text = icon, fontSize = 24.sp)
        }
    }
}

@Composable
fun SettingsSection(
    settings: UserSettings,
    onUpdateTheme: (String) -> Unit,
    onUpdateWalkingGoal: (Int) -> Unit,
    onUpdateFocusGoal: (Int) -> Unit,
    onUpdateSedentary: (Int) -> Unit,
    onUpdateNotification: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        onUpdateNotification(granted)
    }

    CreamPanel {
        Text(
            text = "系统设置",
            style = MaterialTheme.typography.titleSmall,
            color = TitleBlue,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        SettingItem("主题模式", settings.themeMode) {
            val modes = listOf("白天", "夜间", "跟随系统")
            val nextIndex = (modes.indexOf(settings.themeMode) + 1).let { if (it < 0) 0 else it % modes.size }
            onUpdateTheme(modes[nextIndex])
        }

        SettingItem("每日步行目标", "${settings.dailyWalkingGoal} 分钟") {
            onUpdateWalkingGoal(if (settings.dailyWalkingGoal >= 120) 30 else settings.dailyWalkingGoal + 30)
        }

        SettingItem("每日专注目标", "${settings.dailyFocusGoal} 分钟") {
            onUpdateFocusGoal(if (settings.dailyFocusGoal >= 120) 25 else settings.dailyFocusGoal + 25)
        }

        SettingItem("久坐提醒间隔", "${settings.sedentaryReminderMinutes} 分钟") {
            onUpdateSedentary(if (settings.sedentaryReminderMinutes >= 120) 30 else settings.sedentaryReminderMinutes + 30)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "提醒开关", style = MaterialTheme.typography.bodyLarge, color = TextBrown)
            Switch(
                checked = settings.notificationEnabled,
                onCheckedChange = { enabled ->
                    if (!enabled) {
                        onUpdateNotification(false)
                    } else if (
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        onUpdateNotification(true)
                    }
                },
                colors = SwitchDefaults.colors(checkedThumbColor = ForestGreen, checkedTrackColor = ForestGreen.copy(alpha = 0.5f))
            )
        }
    }
}

@Composable
fun SettingItem(label: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = TextBrown)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = value, style = MaterialTheme.typography.bodyMedium, color = TitleBlue, fontWeight = FontWeight.Medium)
            Text(text = " 〉", color = TextBrown.copy(alpha = 0.3f), fontSize = 12.sp)
        }
    }
}

@Composable
fun EditProfileDialog(
    currentNickname: String,
    currentPlanetName: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var nickname by remember { mutableStateOf(currentNickname) }
    var planetName by remember { mutableStateOf(currentPlanetName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("编辑资料", color = TitleBlue, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text("旅人昵称") },
                    placeholder = { Text("星际旅人") },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                )
                TextField(
                    value = planetName,
                    onValueChange = { planetName = it },
                    label = { Text("星球名称") },
                    singleLine = true,
                    isError = planetName.isBlank(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (planetName.isNotBlank()) onConfirm(nickname, planetName) },
                enabled = planetName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消", color = TextBrown) }
        },
        containerColor = Color(0xFFFFFDF4),
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun PrivacyDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("隐私与数据说明", color = TitleBlue, fontWeight = FontWeight.Bold) },
        text = {
            Text(
                text = "1. 数据存储：星球宠物目前是一个纯单机应用，你的所有星球数据、日志和设置均默认保存在本机设备上。\n\n" +
                        "2. 联网权限：我们目前不要求登录，也不上传任何数据到服务器，不做云端同步。\n\n" +
                        "3. 数据清空：如果你卸载应用或在设置中点击“清空数据”，你的所有进度将无法找回。\n\n" +
                        "4. 权限申请：本应用仅在必要时申请通知和身体活动权限，用于提供核心体验。",
                color = TextBrown,
                lineHeight = 20.sp
            )
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = TitleBlue)) {
                Text("我知道了")
            }
        },
        containerColor = Color(0xFFFFFDF4),
        shape = RoundedCornerShape(24.dp)
    )
}
