package com.example.planetlife.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.planetlife.data.repository.AppDataRepository
import com.example.planetlife.data.repository.CollectionRepository
import com.example.planetlife.data.repository.DailyEnergyRepository
import com.example.planetlife.data.repository.DailyStatsRepository
import com.example.planetlife.data.repository.FocusRepository
import com.example.planetlife.data.repository.PlanetEventRepository
import com.example.planetlife.data.repository.PlanetRepository
import com.example.planetlife.data.repository.TaskRepository
import com.example.planetlife.data.settings.SettingsDataStore
import com.example.planetlife.notification.SedentaryReminderNotifier
import com.example.planetlife.ui.collection.CollectionScreen
import com.example.planetlife.ui.collection.CollectionViewModel
import com.example.planetlife.ui.components.PlanetBottomNavigation
import com.example.planetlife.ui.focus.FocusScreen
import com.example.planetlife.ui.focus.FocusViewModel
import com.example.planetlife.ui.home.HomeScreen
import com.example.planetlife.ui.home.HomeViewModel
import com.example.planetlife.ui.logs.LogsScreen
import com.example.planetlife.ui.logs.LogsViewModel
import com.example.planetlife.ui.onboarding.OnboardingScreen
import com.example.planetlife.ui.onboarding.OnboardingViewModel
import com.example.planetlife.ui.profile.ProfileScreen
import com.example.planetlife.ui.profile.ProfileViewModel
import com.example.planetlife.ui.tasks.TasksScreen
import com.example.planetlife.ui.tasks.TasksViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String,
    planetRepository: PlanetRepository,
    dailyStatsRepository: DailyStatsRepository,
    dailyEnergyRepository: DailyEnergyRepository,
    planetEventRepository: PlanetEventRepository,
    focusRepository: FocusRepository,
    taskRepository: TaskRepository,
    collectionRepository: CollectionRepository,
    appDataRepository: AppDataRepository,
    settingsDataStore: SettingsDataStore,
    sedentaryReminderNotifier: SedentaryReminderNotifier,
) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    val showBottomBar = currentDestination?.route != AppRoute.Onboarding.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                PlanetBottomNavigation(
                    tabs = AppRoute.bottomTabs,
                    currentDestination = currentDestination,
                    onTabSelected = { route ->
                        navController.navigate(route.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(AppRoute.Onboarding.route) {
                val onboardingViewModel: OnboardingViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return OnboardingViewModel(planetRepository, settingsDataStore) as T
                        }
                    },
                )
                OnboardingScreen(
                    viewModel = onboardingViewModel,
                    onComplete = {
                        navController.navigate(AppRoute.Home.route) {
                            popUpTo(AppRoute.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(AppRoute.Home.route) {
                val homeViewModel: HomeViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return HomeViewModel(
                                planetRepository,
                                dailyStatsRepository,
                                dailyEnergyRepository,
                                planetEventRepository,
                                taskRepository,
                                collectionRepository,
                                settingsDataStore,
                                sedentaryReminderNotifier,
                            ) as T
                        }
                    },
                )
                HomeScreen(homeViewModel)
            }
            composable(AppRoute.Logs.route) {
                val logsViewModel: LogsViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return LogsViewModel(planetEventRepository) as T
                        }
                    },
                )
                LogsScreen(logsViewModel)
            }
            composable(AppRoute.Focus.route) {
                val focusViewModel: FocusViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return FocusViewModel(
                                focusRepository,
                                planetRepository,
                                dailyStatsRepository,
                                planetEventRepository,
                                taskRepository,
                                collectionRepository
                            ) as T
                        }
                    },
                )
                FocusScreen(focusViewModel)
            }
            composable(AppRoute.Tasks.route) {
                val tasksViewModel: TasksViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return TasksViewModel(
                                taskRepository,
                                planetRepository,
                                dailyStatsRepository,
                                planetEventRepository,
                                collectionRepository
                            ) as T
                        }
                    },
                )
                TasksScreen(tasksViewModel)
            }
            composable(AppRoute.Profile.route) {
                val profileViewModel: ProfileViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return ProfileViewModel(
                                planetRepository,
                                dailyStatsRepository,
                                collectionRepository,
                                settingsDataStore,
                                appDataRepository
                            ) as T
                        }
                    },
                )
                ProfileScreen(
                    viewModel = profileViewModel,
                    onNavigateToCollection = {
                        navController.navigate(AppRoute.Collection.route)
                    },
                    onDataCleared = {
                        navController.navigate(AppRoute.Onboarding.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(AppRoute.Collection.route) {
                val collectionViewModel: CollectionViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return CollectionViewModel(collectionRepository) as T
                        }
                    },
                )
                CollectionScreen(
                    viewModel = collectionViewModel,
                    onBack = {
                        if (!navController.popBackStack()) {
                            navController.navigate(AppRoute.Profile.route) {
                                launchSingleTop = true
                            }
                        }
                    },
                )
            }
        }
    }
}
