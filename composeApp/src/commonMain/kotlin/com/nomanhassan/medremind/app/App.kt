package com.nomanhassan.medremind.app

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.nomanhassan.medremind.app.navigation.NotificationNavigator
import com.nomanhassan.medremind.app.navigation.Route
import com.nomanhassan.medremind.app.ui.theme.MedRemindTheme
import com.nomanhassan.medremind.domain.settings.LocalizedLanguage
import com.nomanhassan.medremind.domain.settings.Localization
import com.nomanhassan.medremind.domain.settings.ThemePreference
import com.nomanhassan.medremind.presentation.add_edit_medication.AddEditMedicationScreenRoot
import com.nomanhassan.medremind.presentation.add_edit_medication.AddEditMedicationViewModel
import com.nomanhassan.medremind.presentation.medication_detail.MedicationDetailScreenRoot
import com.nomanhassan.medremind.presentation.medication_detail.MedicationDetailViewModel
import com.nomanhassan.medremind.presentation.medication_list.MedicationListScreenRoot
import com.nomanhassan.medremind.presentation.medication_list.MedicationListViewModel
import com.nomanhassan.medremind.presentation.medication_reminder.ReminderScreenRoot
import com.nomanhassan.medremind.presentation.medication_reminder.ReminderViewModel
import com.nomanhassan.medremind.presentation.medication_settings.SettingsScreenRoot
import com.nomanhassan.medremind.presentation.medication_settings.SettingsViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    val settingsViewModel = koinViewModel<SettingsViewModel>()
    val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()
    
    val localization = koinInject<Localization>()
    LaunchedEffect(settingsState.language.code) {
        localization.applyLanguage(settingsState.language.code)
    }

    AnimatedContent(
        targetState = settingsState.isLoading,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "SplashToContent"
    ) { isLoading ->
        if (isLoading) {
            SplashScreen()
        } else {
            val darkTheme = when (settingsState.themePreference) {
                ThemePreference.SYSTEM -> isSystemInDarkTheme()
                ThemePreference.DARK -> true
                ThemePreference.LIGHT -> false
            }

            CompositionLocalProvider(LocalizedLanguage provides settingsState.language) {
                MedRemindTheme(
                    darkTheme = darkTheme,
                    contrastMode = settingsState.contrastMode
                ) {
                    val navController = rememberNavController()

                    val targetReminderTimeId by NotificationNavigator.targetReminderTimeId.collectAsStateWithLifecycle()
                    LaunchedEffect(targetReminderTimeId) {
                        targetReminderTimeId?.let {
                            navController.navigate(Route.MedicationReminder(it))
                            NotificationNavigator.clearTarget()
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = Route.MedicationGraph,
//                        enterTransition = {
//                            slideInHorizontally(
//                                initialOffsetX = { 1000 },
//                                animationSpec = tween(300)
//                            ) + fadeIn(
//                                animationSpec = tween(300)
//                            )
//                        },
//                        exitTransition = {
//                            slideOutHorizontally(
//                                targetOffsetX = { -1000 },
//                                animationSpec = tween(300)
//                            ) + fadeOut(
//                                animationSpec = tween(300)
//                            )
//                        },
//                        popEnterTransition = {
//                            slideInHorizontally(
//                                initialOffsetX = { -1000 },
//                                animationSpec = tween(300)
//                            ) + fadeIn(
//                                animationSpec = tween(300)
//                            )
//                        },
//                        popExitTransition = {
//                            slideOutHorizontally(
//                                targetOffsetX = { 1000 },
//                                animationSpec = tween(300)
//                            ) + fadeOut(
//                                animationSpec = tween(300)
//                            )
//                        }
                    ) {
                        navigation<Route.MedicationGraph>(
                            startDestination = Route.MedicationList
                        ) {
                            composable<Route.MedicationList> {
                                val viewModel = koinViewModel<MedicationListViewModel>()
                                MedicationListScreenRoot(
                                    viewModel = viewModel,
                                    onClickMedicationItem = { id ->
                                        navController.navigate(Route.MedicationDetail(id))
                                    },
                                    onClickSettings = {
                                        navController.navigate(Route.MedicationSettings)
                                    },
                                    onClickAddMedication = {
                                        navController.navigate(Route.AddEditMedication(null))
                                    }
                                )
                            }

                            composable<Route.MedicationDetail> {
                                val viewModel = koinViewModel<MedicationDetailViewModel>()
                                MedicationDetailScreenRoot(
                                    viewModel = viewModel,
                                    onClickEditMedication = { id ->
                                        navController.navigate(Route.AddEditMedication(id))
                                    },
                                    onBackClicked = {
                                        navController.navigateUp()
                                    }
                                )
                            }

                            composable<Route.AddEditMedication> {
                                val viewModel = koinViewModel<AddEditMedicationViewModel>()
                                AddEditMedicationScreenRoot(
                                    viewModel = viewModel,
                                    onGoBack = {
                                        navController.navigateUp()
                                    }
                                )
                            }

                            composable<Route.MedicationSettings> {
                                val viewModel = koinViewModel<SettingsViewModel>()
                                SettingsScreenRoot(
                                    viewModel = viewModel,
                                    onGoBack = {
                                        navController.navigateUp()
                                    }
                                )
                            }

                            composable<Route.MedicationReminder> {
                                val viewModel = koinViewModel<ReminderViewModel>()
                                ReminderScreenRoot(
                                    viewModel = viewModel,
                                    onGoBack = {
                                        navController.navigate(Route.MedicationList) {
                                            popUpTo(Route.MedicationList) { inclusive = true }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}