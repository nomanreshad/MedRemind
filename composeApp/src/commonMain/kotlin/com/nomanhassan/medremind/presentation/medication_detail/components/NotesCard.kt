package com.nomanhassan.medremind.presentation.medication_detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nomanhassan.medremind.core.presentation.components.Header
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.label_notes
import org.jetbrains.compose.resources.stringResource

@Composable
fun NotesCard(
    notes: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .widthIn(max = 500.dp)
            .fillMaxWidth()
    ) {
        Header(label = stringResource(Res.string.label_notes))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 4.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Text(
                text = notes,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 20.dp
                )
            )
        }
    }
}