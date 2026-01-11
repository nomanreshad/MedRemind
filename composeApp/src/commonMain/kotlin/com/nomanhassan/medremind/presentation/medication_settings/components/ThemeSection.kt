package com.nomanhassan.medremind.presentation.medication_settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nomanhassan.medremind.app.ui.theme.MedRemindTheme
import com.nomanhassan.medremind.domain.settings.ContrastMode
import com.nomanhassan.medremind.domain.settings.Language
import com.nomanhassan.medremind.domain.settings.ThemePreference
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.icon_dark_theme
import medremind.composeapp.generated.resources.icon_high_contrast
import medremind.composeapp.generated.resources.icon_language
import medremind.composeapp.generated.resources.icon_light_theme
import medremind.composeapp.generated.resources.icon_medium_contrast
import medremind.composeapp.generated.resources.icon_standard_contrast
import medremind.composeapp.generated.resources.icon_system_theme
import medremind.composeapp.generated.resources.language_bangla
import medremind.composeapp.generated.resources.language_device_language
import medremind.composeapp.generated.resources.language_english
import medremind.composeapp.generated.resources.language_german
import medremind.composeapp.generated.resources.language_match_prescription
import medremind.composeapp.generated.resources.settings_appearance_dark
import medremind.composeapp.generated.resources.settings_appearance_light
import medremind.composeapp.generated.resources.settings_appearance_system
import medremind.composeapp.generated.resources.settings_contrast_high
import medremind.composeapp.generated.resources.settings_contrast_medium
import medremind.composeapp.generated.resources.settings_contrast_normal
import medremind.composeapp.generated.resources.settings_section_ai_analysis_language
import medremind.composeapp.generated.resources.settings_section_appearance
import medremind.composeapp.generated.resources.settings_section_contrast
import medremind.composeapp.generated.resources.settings_section_language
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun <T> ThemeSection(
    title: String,
    current: T,
    options: List<T>,
    icons: Map<T, Painter>,
    onChange: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            options.forEach { option ->
                val icon = icons[option]
                val textRes = when (option) {
                    ThemePreference.SYSTEM -> Res.string.settings_appearance_system
                    ThemePreference.LIGHT -> Res.string.settings_appearance_light
                    ThemePreference.DARK -> Res.string.settings_appearance_dark
                    ContrastMode.NORMAL -> Res.string.settings_contrast_normal
                    ContrastMode.MEDIUM -> Res.string.settings_contrast_medium
                    ContrastMode.HIGH -> Res.string.settings_contrast_high
                    Language.AUTO -> Res.string.language_match_prescription
                    Language.DEVICE -> Res.string.language_device_language
                    Language.ENGLISH -> Res.string.language_english
                    Language.BANGLA -> Res.string.language_bangla
                    Language.GERMAN -> Res.string.language_german
                    else -> null
                }
                
                if (icon != null && textRes != null) {
                    ThemeButton(
                        icon = icon,
                        text = stringResource(textRes),
                        isSelected = current == option,
                        onClick = { onChange(option) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeButton(
    icon: Painter,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline
    }

    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
    } else {
        Color.Transparent
    }

    Column(
        modifier = modifier
            .width(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
    ) {
        OutlinedButton(
            onClick = onClick,
            contentPadding = PaddingValues(14.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.outlinedButtonColors(containerColor = backgroundColor),
            border = BorderStroke(1.dp, borderColor)
        ) {
            Icon(
                painter = icon,
                contentDescription = text,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ThemeSectionPreview() {
    MedRemindTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ThemeSection(
                title = stringResource(Res.string.settings_section_appearance),
                current = ThemePreference.SYSTEM,
                options = ThemePreference.entries,
                icons = mapOf(
                    ThemePreference.SYSTEM to painterResource(Res.drawable.icon_system_theme),
                    ThemePreference.LIGHT to painterResource(Res.drawable.icon_light_theme),
                    ThemePreference.DARK to painterResource(Res.drawable.icon_dark_theme)
                ),
                onChange = {}
            )
            
            ThemeSection(
                title = stringResource(Res.string.settings_section_contrast),
                current = ContrastMode.NORMAL,
                options = ContrastMode.entries,
                icons = mapOf(
                    ContrastMode.NORMAL to painterResource(Res.drawable.icon_standard_contrast),
                    ContrastMode.MEDIUM to painterResource(Res.drawable.icon_medium_contrast),
                    ContrastMode.HIGH to painterResource(Res.drawable.icon_high_contrast)
                ),
                onChange = {}
            )
        }
    }
}