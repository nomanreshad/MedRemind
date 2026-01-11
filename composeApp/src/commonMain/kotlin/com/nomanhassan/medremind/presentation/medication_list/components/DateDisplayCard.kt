package com.nomanhassan.medremind.presentation.medication_list.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nomanhassan.medremind.core.util.toLocalizedDigits
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DateDisplayCard(
    dayOfWeek: String,
    dayOfMonth: String,
    month: String,
    year: String,
    isCollapsed: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = if (isCollapsed) MaterialTheme.shapes.large else MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        AnimatedContent(
            targetState = isCollapsed,
            transitionSpec = {
                if (targetState) {
                    // Transitioning to Collapsed (Slide up and fade)
                    (slideInVertically { height -> height } + fadeIn()) togetherWith
                            slideOutVertically { height -> -height } + fadeOut()
                } else {
                    // Transitioning to Expanded (Slide down and fade)
                    (slideInVertically { height -> -height } + fadeIn()) togetherWith
                            slideOutVertically { height -> height } + fadeOut()
                }.using(
                    SizeTransform(clip = false)
                )
            },
            label = "DateCardAnimation"
        ) { collapsed ->
            if (collapsed) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "$dayOfWeek, $dayOfMonth $month $year".toLocalizedDigits(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = dayOfWeek,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = dayOfMonth.toLocalizedDigits(),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$month ${year.toLocalizedDigits()}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun DateDisplayCardPrev() {
    DateDisplayCard(
        dayOfWeek = "SATURDAY",
        dayOfMonth = "8",
        month = "November",
        year = "2025",
        isCollapsed = false
    )
}

@Preview
@Composable
private fun DateDisplayCardPrev2() {
    DateDisplayCard(
        dayOfWeek = "SATURDAY",
        dayOfMonth = "8",
        month = "November",
        year = "2025",
        isCollapsed = true
    )
}