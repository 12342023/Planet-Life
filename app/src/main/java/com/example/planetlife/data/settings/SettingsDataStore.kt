package com.example.planetlife.data.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.planetLifeDataStore by preferencesDataStore(name = "planet_life_settings")

class SettingsDataStore(
    private val context: Context,
) {
    val settings: Flow<UserSettings> = context.planetLifeDataStore.data.map { preferences ->
        UserSettings(
            nickname = preferences[Keys.Nickname] ?: "星际旅人",
            themeMode = preferences[Keys.ThemeMode] ?: "跟随系统",
            dailyWalkingGoal = preferences[Keys.DailyWalkingGoal] ?: 30,
            dailyFocusGoal = preferences[Keys.DailyFocusGoal] ?: 25,
            sedentaryReminderMinutes = preferences[Keys.SedentaryReminderMinutes] ?: 60,
            idealSleepHour = preferences[Keys.IdealSleepHour] ?: 23,
            notificationEnabled = preferences[Keys.NotificationEnabled] ?: false,
        )
    }

    suspend fun updateNickname(nickname: String) {
        context.planetLifeDataStore.edit { preferences ->
            preferences[Keys.Nickname] = nickname.ifBlank { "星际旅人" }
        }
    }

    suspend fun updateThemeMode(themeMode: String) {
        context.planetLifeDataStore.edit { preferences ->
            preferences[Keys.ThemeMode] = themeMode
        }
    }

    suspend fun updateDailyWalkingGoal(goal: Int) {
        context.planetLifeDataStore.edit { preferences ->
            preferences[Keys.DailyWalkingGoal] = goal
        }
    }

    suspend fun updateDailyFocusGoal(goal: Int) {
        context.planetLifeDataStore.edit { preferences ->
            preferences[Keys.DailyFocusGoal] = goal
        }
    }

    suspend fun updateSedentaryReminderMinutes(minutes: Int) {
        context.planetLifeDataStore.edit { preferences ->
            preferences[Keys.SedentaryReminderMinutes] = minutes
        }
    }

    suspend fun updateNotificationEnabled(enabled: Boolean) {
        context.planetLifeDataStore.edit { preferences ->
            preferences[Keys.NotificationEnabled] = enabled
        }
    }

    suspend fun clearData() {
        context.planetLifeDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private object Keys {
        val Nickname: Preferences.Key<String> = stringPreferencesKey("nickname")
        val ThemeMode: Preferences.Key<String> = stringPreferencesKey("theme_mode")
        val DailyWalkingGoal: Preferences.Key<Int> = intPreferencesKey("daily_walking_goal")
        val DailyFocusGoal: Preferences.Key<Int> = intPreferencesKey("daily_focus_goal")
        val SedentaryReminderMinutes: Preferences.Key<Int> = intPreferencesKey("sedentary_reminder_minutes")
        val IdealSleepHour: Preferences.Key<Int> = intPreferencesKey("ideal_sleep_hour")
        val NotificationEnabled: Preferences.Key<Boolean> = booleanPreferencesKey("notification_enabled")
    }
}
