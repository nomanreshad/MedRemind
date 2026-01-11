@file:OptIn(ExperimentalMaterial3Api::class)

package com.nomanhassan.medremind.presentation.medication_settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nomanhassan.medremind.app.ui.theme.MedRemindTheme
import com.nomanhassan.medremind.domain.settings.ContrastMode
import com.nomanhassan.medremind.domain.settings.Language
import com.nomanhassan.medremind.domain.settings.ThemePreference
import com.nomanhassan.medremind.presentation.medication_settings.components.LanguageSection
import com.nomanhassan.medremind.presentation.medication_settings.components.ThemeSection
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.btn_back_description
import medremind.composeapp.generated.resources.icon_high_contrast
import medremind.composeapp.generated.resources.icon_medium_contrast
import medremind.composeapp.generated.resources.icon_light_theme
import medremind.composeapp.generated.resources.icon_dark_theme
import medremind.composeapp.generated.resources.icon_language
import medremind.composeapp.generated.resources.icon_outline_settings
import medremind.composeapp.generated.resources.icon_rounded_arrow_go_back
import medremind.composeapp.generated.resources.icon_standard_contrast
import medremind.composeapp.generated.resources.icon_system_theme
import medremind.composeapp.generated.resources.language_bangla
import medremind.composeapp.generated.resources.language_device_language
import medremind.composeapp.generated.resources.language_english
import medremind.composeapp.generated.resources.language_german
import medremind.composeapp.generated.resources.language_match_prescription
import medremind.composeapp.generated.resources.settings_section_ai_analysis_language
import medremind.composeapp.generated.resources.settings_section_appearance
import medremind.composeapp.generated.resources.settings_section_contrast
import medremind.composeapp.generated.resources.settings_section_language
import medremind.composeapp.generated.resources.settings_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreenRoot(
    viewModel: SettingsViewModel = koinViewModel(),
    onGoBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsScreen(
        state = state,
        onAction = { action ->
            when (action) {
                MedicationSettingsAction.OnClickGoBack -> onGoBack()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
private fun SettingsScreen(
    state: SettingsState,
    onAction: (MedicationSettingsAction) -> Unit
) {
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.primary
                            )
                        )
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(Res.string.settings_title)
                        )
                    },
                    navigationIcon = {
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                            tooltip = {
                                PlainTooltip {
                                    Text(
                                        text = stringResource(Res.string.btn_back_description),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            },
                            state = rememberTooltipState(),
                        ) {
                            IconButton(
                                onClick = { onAction(MedicationSettingsAction.OnClickGoBack) }
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.icon_rounded_arrow_go_back),
                                    contentDescription = stringResource(Res.string.btn_back_description),
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.primary
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                ThemeSection(
                    title = stringResource(Res.string.settings_section_appearance),
                    current = state.themePreference,
                    options = ThemePreference.entries,
                    icons = mapOf(
                        ThemePreference.SYSTEM to painterResource(Res.drawable.icon_system_theme),
                        ThemePreference.LIGHT to painterResource(Res.drawable.icon_light_theme),
                        ThemePreference.DARK to painterResource(Res.drawable.icon_dark_theme)
                    ),
                    onChange = {
                        onAction(MedicationSettingsAction.OnThemeChange(it))
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                )

                ThemeSection(
                    title = stringResource(Res.string.settings_section_contrast),
                    current = state.contrastMode,
                    options = ContrastMode.entries,
                    icons = mapOf(
                        ContrastMode.NORMAL to painterResource(Res.drawable.icon_standard_contrast),
                        ContrastMode.MEDIUM to painterResource(Res.drawable.icon_medium_contrast),
                        ContrastMode.HIGH to painterResource(Res.drawable.icon_high_contrast)
                    ),
                    onChange = {
                        onAction(MedicationSettingsAction.OnContrastChange(it))
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                )

                LanguageSection(
                    title = stringResource(Res.string.settings_section_language),
                    currentSelection = state.language,
                    options = Language.entries.filterNot { it == Language.AUTO || it == Language.DEVICE },
                    optionToString = { language ->
                        when (language) {
                            Language.ENGLISH -> stringResource(Res.string.language_english)
                            Language.BANGLA -> stringResource(Res.string.language_bangla)
                            Language.GERMAN -> stringResource(Res.string.language_german)
                            else -> ""
                        }
                    },
                    onSelectionChange = {
                        onAction(MedicationSettingsAction.OnLanguageChange(it))
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                )

                LanguageSection(
                    title = stringResource(Res.string.settings_section_ai_analysis_language),
                    currentSelection = state.aiLanguage,
                    options = Language.entries,
                    optionToString = { language ->
                        when (language) {
                            Language.AUTO -> stringResource(Res.string.language_match_prescription)
                            Language.DEVICE -> stringResource(Res.string.language_device_language)
                            Language.ENGLISH -> stringResource(Res.string.language_english)
                            Language.BANGLA -> stringResource(Res.string.language_bangla)
                            Language.GERMAN -> stringResource(Res.string.language_german)
                        }
                    },
                    onSelectionChange = {
                        onAction(MedicationSettingsAction.OnAiLanguageChange(it))
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun SettingsScreenPrev() {
    MedRemindTheme {
        SettingsScreen(
            state = SettingsState(),
            onAction = {}
        )
    }
}