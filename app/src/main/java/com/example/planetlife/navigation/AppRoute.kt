package com.example.planetlife.navigation

import androidx.annotation.StringRes
import com.example.planetlife.R

sealed class AppRoute(
    val route: String,
    @param:StringRes val labelRes: Int,
    val iconText: String,
) {
    data object Home : AppRoute("home", R.string.nav_planet, "星")
    data object Logs : AppRoute("logs", R.string.nav_logs, "志")
    data object Focus : AppRoute("focus", R.string.nav_focus, "专")
    data object Tasks : AppRoute("tasks", R.string.nav_tasks, "任")
    data object Profile : AppRoute("profile", R.string.nav_profile, "我")
    data object Onboarding : AppRoute("onboarding", R.string.app_name, "")
    data object Collection : AppRoute("collection", R.string.nav_collection, "鉴")

    companion object {
        val bottomTabs: List<AppRoute>
            get() = listOf(Home, Logs, Focus, Tasks, Profile)
    }
}
