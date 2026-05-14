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
import java.time.LocalDate

private val Context.planetLifeDataStore by preferencesDataStore(name = "planet_life_settings")

data class StarVisitReward(
    val rewarded: Boolean,
    val streak: Int,
    val bonus: Int,
    val total: Int,
)

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

    suspend fun recordDailyVisit(date: String): StarVisitReward {
        var reward = StarVisitReward(
            rewarded = false,
            streak = 1,
            bonus = 0,
            total = 0,
        )
        context.planetLifeDataStore.edit { preferences ->
            val lastVisitDate = preferences[Keys.StarLastVisitDate]
            val currentStreak = preferences[Keys.StarVisitStreak] ?: 0
            val streak = calculateVisitStreak(lastVisitDate, date, currentStreak)
            val alreadyRewarded = preferences[Keys.StarVisitRewardDate] == date
            val bonus = calculateStreakBonus(streak)
            val total = if (alreadyRewarded) 0 else 1 + bonus

            preferences[Keys.StarLastVisitDate] = date
            preferences[Keys.StarVisitStreak] = streak
            if (!alreadyRewarded) {
                preferences[Keys.StarVisitRewardDate] = date
            }
            reward = StarVisitReward(
                rewarded = !alreadyRewarded,
                streak = streak,
                bonus = if (alreadyRewarded) 0 else bonus,
                total = total,
            )
        }
        return reward
    }

    suspend fun recordMoodStarReward(date: String): Boolean {
        var rewarded = false
        context.planetLifeDataStore.edit { preferences ->
            if (preferences[Keys.StarMoodRewardDate] != date) {
                preferences[Keys.StarMoodRewardDate] = date
                rewarded = true
            }
        }
        return rewarded
    }

    suspend fun clearData() {
        context.planetLifeDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private fun calculateVisitStreak(
        lastVisitDate: String?,
        date: String,
        currentStreak: Int,
    ): Int {
        if (lastVisitDate == date) {
            return currentStreak.coerceAtLeast(1)
        }
        val isNextDay = lastVisitDate != null && runCatching {
            LocalDate.parse(lastVisitDate).plusDays(1) == LocalDate.parse(date)
        }.getOrDefault(false)
        return if (isNextDay) {
            (currentStreak + 1).coerceAtLeast(1)
        } else {
            1
        }
    }

    private fun calculateStreakBonus(streak: Int): Int {
        return when (streak) {
            7 -> 8
            3 -> 3
            else -> 0
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
        val StarLastVisitDate: Preferences.Key<String> = stringPreferencesKey("star_last_visit_date")
        val StarVisitStreak: Preferences.Key<Int> = intPreferencesKey("star_visit_streak")
        val StarVisitRewardDate: Preferences.Key<String> = stringPreferencesKey("star_visit_reward_date")
        val StarMoodRewardDate: Preferences.Key<String> = stringPreferencesKey("star_mood_reward_date")
    }
}
