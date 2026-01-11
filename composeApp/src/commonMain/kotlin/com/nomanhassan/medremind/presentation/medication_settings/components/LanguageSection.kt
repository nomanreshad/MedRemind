@file:OptIn(ExperimentalMaterial3Api::class)

package com.nomanhassan.medremind.presentation.medication_settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.nomanhassan.medremind.app.ui.theme.MedRemindTheme
import com.nomanhassan.medremind.domain.settings.Language
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.btn_cancel
import medremind.composeapp.generated.resources.btn_ok
import medremind.composeapp.generated.resources.icon_language
import medremind.composeapp.generated.resources.language_bangla
import medremind.composeapp.generated.resources.language_device_language
import medremind.composeapp.generated.resources.language_english
import medremind.composeapp.generated.resources.language_german
import medremind.composeapp.generated.resources.language_match_prescription
import medremind.composeapp.generated.resources.settings_section_ai_analysis_language
import medremind.composeapp.generated.resources.settings_section_language
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun <T> LanguageSection(
    title: String,
    currentSelection: T,
    options: List<T>,
    optionToString: @Composable (T) -> String,
    onSelectionChange: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDialogOpen by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .widthIn(max = 500.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { isDialogOpen = true },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.icon_language),
                        contentDescription = title,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = optionToString(currentSelection),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        modifier = Modifier
                            .basicMarquee(iterations = Int.MAX_VALUE)
                    )
                }
            }

            if (isDialogOpen) {
                LanguageSelectionDialog(
                    title = title,
                    currentSelection = currentSelection,
                    options = options,
                    optionToString = optionToString,
                    onSelectionConfirmed = { newSelection ->
                        onSelectionChange(newSelection)
                        isDialogOpen = false
                    },
                    onDismiss = { isDialogOpen = false }
                )
            }
        }
    }
}

@Composable
private fun <T> LanguageSelectionDialog(
    title: String,
    currentSelection: T,
    options: List<T>,
    optionToString: @Composable (T) -> String,
    onSelectionConfirmed: (T) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var tempSelectedOption by remember { mutableStateOf(currentSelection) }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(28.dp)
                )
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
            ) {
                items(options) { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { tempSelectedOption = option }
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (option == tempSelectedOption),
                            onClick = { tempSelectedOption = option }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = optionToString(option),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(Res.string.btn_cancel))
                }
                Spacer(Modifier.width(8.dp))
                TextButton(
                    onClick = { onSelectionConfirmed(tempSelectedOption) }
                ) {
                    Text(stringResource(Res.string.btn_ok))
                }
            }
        }
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
            LanguageSection(
                title = stringResource(Res.string.settings_section_language),
                currentSelection = Language.ENGLISH,
                options = Language.entries.filterNot { it == Language.AUTO || it == Language.DEVICE },
                optionToString = { language ->
                    when (language) {
                        Language.ENGLISH -> stringResource(Res.string.language_english)
                        Language.BANGLA -> stringResource(Res.string.language_bangla)
                        Language.GERMAN -> stringResource(Res.string.language_german)
                        else -> "Unknown"
                    }
                },
                onSelectionChange = {}
            )
            
            LanguageSection(
                title = stringResource(Res.string.settings_section_ai_analysis_language),
                currentSelection = Language.ENGLISH,
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
                onSelectionChange = {}
            )
        }
    }
}