@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)

package com.nomanhassan.medremind.presentation.add_edit_medication.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.nomanhassan.medremind.app.ui.theme.MedRemindTheme
import kotlinx.coroutines.delay
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.ai_prescription_scan_message_1
import medremind.composeapp.generated.resources.ai_prescription_scan_message_2
import medremind.composeapp.generated.resources.ai_prescription_scan_message_3
import medremind.composeapp.generated.resources.ai_prescription_scan_message_4
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun AiLoadingDialog(
    loadingTexts: List<String>
) {
    var textIndex by remember { mutableIntStateOf(0) }

    // 1. Setup the Infinite Transition for the border rotation
    val infiniteTransition = rememberInfiniteTransition(label = "borderRotation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing)
        ),
        label = "angle"
    )

    // 2. "Single Snake" Gradient Logic
//    val gradientBrush = Brush.sweepGradient(
//        colorStops = arrayOf(
//            // Snake 1
//            0.0f to Color.Transparent,
//            0.75f to Color.Transparent,
//            0.9f to MaterialTheme.colorScheme.secondary, // Tail color
//            1.0f to MaterialTheme.colorScheme.primary    // Head color
//        )
//    )

    // 2. "Double Snake" Gradient Logic
    val gradientBrush = Brush.sweepGradient(
        colorStops = arrayOf(
            0.0f to Color.Transparent, 0.4f to Color.Transparent,
            0.5f to MaterialTheme.colorScheme.primary, // Snake 1
            0.55f to Color.Transparent, 0.9f to Color.Transparent,
            1.0f to MaterialTheme.colorScheme.primary  // Snake 2
        )
    )
    
    val animatedColor by animateColorAsState(
        targetValue = if (textIndex > loadingTexts.size / 2)
            MaterialTheme.colorScheme.secondary
        else
            MaterialTheme.colorScheme.primary,
        animationSpec = tween(1000),
        label = "colorShift"
    )

    LaunchedEffect(loadingTexts) {
        if (loadingTexts.isNotEmpty()) {
            while (true) {
                delay(4000)
                textIndex = (textIndex + 1) % loadingTexts.size
            }
        }
    }

    BasicAlertDialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .drawBehind {
                    rotate(angle) {
                        // Draw a circle large enough to cover the rectangular corners
                        drawCircle(
                            brush = gradientBrush,
                            radius = size.maxDimension,
                        )
                    }
                }
                .padding(3.dp)
                .clip(RoundedCornerShape(21.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                AiProgressIndicator(
                    modifier = Modifier.size(60.dp),
                    color = animatedColor
                )

                FixedSizeContent(
                    contentToMeasure = {
                        loadingTexts.forEach { text ->
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                ) {
                    AnimatedContent(
                        targetState = textIndex,
                        transitionSpec = {
                            (slideInVertically { h -> h } + fadeIn())
                                .togetherWith(slideOutVertically { h -> -h } + fadeOut())
                        },
                        label = "textAnimation"
                    ) { currentIndex ->
                        Text(
                            text = loadingTexts.getOrNull(currentIndex) ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

/**
 * An advanced AI-themed loading indicator mimicking a neural network extracting data.
 * * Key Features:
 * - Organic "Breathing" Core: Layers a base pulse with high-frequency micro-jitters.
 * - Neural Synapse Flickering: Simulates electrical activity with dynamic line thickness/alpha.
 * - Data Extraction Streams: Animates particles flowing from satellite nodes into the core.
 * - Contextual Color Shifting: Transitions between theme colors based on the current text index.
 */
@Composable
private fun AiProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "NeuralNetwork")

    // 1. Organic Breathing Pulse (Base breathing + Brain-activity jitter)
    val basePulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "basePulse"
    )
    val microJitter by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(80, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "microJitter"
    )

    // 2. Neural Synapse Flickering (Electrical signals)
    val synapseFlicker by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(150, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "synapseFlicker"
    )

    // Orbit Rotation
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing)
        ),
        label = "orbitRotation"
    )

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val orbitRadius = size.minDimension / 2.5f
        val coreScale = basePulse * microJitter

        // Draw Ambient Core Glow
        drawCircle(
            color = color.copy(alpha = 0.1f),
            radius = (size.minDimension / 3.5f) * coreScale,
            center = center
        )

        // Draw Main Core (The "Brain")
        drawCircle(
            color = color,
            radius = (size.minDimension / 8) * coreScale,
            center = center
        )

        val nodeCount = 3
        for (i in 0 until nodeCount) {
            val angleDeg = rotation + (i * (360f / nodeCount))
            val angleRad = angleDeg * (PI / 180).toFloat()

            val nodePos = Offset(
                x = center.x + orbitRadius * cos(angleRad),
                y = center.y + orbitRadius * sin(angleRad)
            )

            // Draw Synapse Connection (With Flicker)
            drawLine(
                color = color.copy(alpha = synapseFlicker),
                start = center,
                end = nodePos,
                strokeWidth = 2.dp.toPx() * microJitter,
                cap = StrokeCap.Round
            )

            // 3. Particle Data Streams (Extraction Effect)
            // Used two particles per node for a continuous flow feel
            for (p in 0 until 2) {
                // Stagger the particles using time and index
                val pProgress = (Clock.System.now().toEpochMilliseconds() + (i * 300) + (p * 500)) % 1000 / 1000f

                // Calculate position from Node towards Center
                val pX = nodePos.x + (center.x - nodePos.x) * pProgress
                val pY = nodePos.y + (center.y - nodePos.y) * pProgress

                // Draw the data particle (shrinks and fades as it's "absorbed" by core)
                drawCircle(
                    color = color.copy(alpha = (1f - pProgress) * 0.8f),
                    radius = 1.5.dp.toPx() * (1f - (pProgress / 2)),
                    center = Offset(pX, pY)
                )
            }

            // Draw Satellite Node
            drawCircle(
                color = color,
                radius = 4.dp.toPx(),
                center = nodePos
            )
        }
    }
}

/**
 * A layout that measures a set of 'subcomposables' to determine the maximum width
 * and height, and then uses those dimensions to lay out the main 'content'.
 *
 * This is the definitive way to prevent layout size changes when swapping
 * content of different sizes, like animating text.
 */
@Composable
private fun FixedSizeContent(
    modifier: Modifier = Modifier,
    contentToMeasure: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        val placeables = subcompose(slotId = "contentToMeasure", contentToMeasure).map {
            it.measure(constraints)
        }

        val maxSize = placeables.fold(initial = Constraints(0, 0)) { currentMax, placeable ->
            Constraints(
                minWidth = max(currentMax.minWidth, placeable.width),
                minHeight = max(currentMax.minHeight, placeable.height)
            )
        }

        val mainContentPlaceable = subcompose(slotId = "content", content).map {
            it.measure(Constraints.fixed(maxSize.minWidth, maxSize.minHeight))
        }.first()

        layout(mainContentPlaceable.width, mainContentPlaceable.height) {
            mainContentPlaceable.place(0, 0)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AiLoadingDialogPrev() {
    MedRemindTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            AiLoadingDialog(
                loadingTexts = listOf(
                    stringResource(Res.string.ai_prescription_scan_message_1),
                    stringResource(Res.string.ai_prescription_scan_message_2),
                    stringResource(Res.string.ai_prescription_scan_message_3),
                    stringResource(Res.string.ai_prescription_scan_message_4)
                )
            )
        }
    }
}