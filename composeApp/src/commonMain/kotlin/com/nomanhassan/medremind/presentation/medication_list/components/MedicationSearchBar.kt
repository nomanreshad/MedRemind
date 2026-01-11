package com.nomanhassan.medremind.presentation.medication_list.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.btn_clear_search_text_description
import medremind.composeapp.generated.resources.icon_outline_rounded_cancel
import medremind.composeapp.generated.resources.icon_outline_search
import medremind.composeapp.generated.resources.icon_search_description
import medremind.composeapp.generated.resources.search_hint
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun MedicationSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onImeSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(
        LocalTextSelectionColors provides TextSelectionColors(
            handleColor = MaterialTheme.colorScheme.onSurface,
            backgroundColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    ) {
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = {
                Text(
                    text = stringResource(Res.string.search_hint)
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(Res.drawable.icon_outline_search),
                    contentDescription = stringResource(Res.string.icon_search_description)
                )
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = searchQuery.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    IconButton(onClick = onClearSearch) {
                        Icon(
                            painter = painterResource(Res.drawable.icon_outline_rounded_cancel),
                            contentDescription = stringResource(Res.string.btn_clear_search_text_description)
                        )
                    }
                }
            },
            shape = RoundedCornerShape(100),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onImeSearch() }
            ),
            modifier = modifier
                .background(
                    shape = RoundedCornerShape(100),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
                .minimumInteractiveComponentSize()
        )
    }
}