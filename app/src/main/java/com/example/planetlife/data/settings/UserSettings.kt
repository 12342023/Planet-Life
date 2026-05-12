package com.example.planetlife.data.settings

data class UserSettings(
    val nickname: String = "星际旅人",
    val themeMode: String = "跟随系统",
    val dailyWalkingGoal: Int = 30,
    val dailyFocusGoal: Int = 25,
    val sedentaryReminderMinutes: Int = 60,
    val idealSleepHour: Int = 23,
    val notificationEnabled: Boolean = false,
)
