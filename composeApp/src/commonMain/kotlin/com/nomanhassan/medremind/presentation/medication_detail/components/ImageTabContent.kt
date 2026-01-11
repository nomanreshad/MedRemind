package com.nomanhassan.medremind.presentation.medication_detail.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.nomanhassan.medremind.core.enums.ImageType
import com.nomanhassan.medremind.core.util.DeviceConfiguration
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.carbonimage
import medremind.composeapp.generated.resources.failed_to_load
import medremind.composeapp.generated.resources.icon_filled_gallery_upload
import medremind.composeapp.generated.resources.icon_no_photo_description
import medremind.composeapp.generated.resources.image_tab_medication
import medremind.composeapp.generated.resources.image_tab_medication_description
import medremind.composeapp.generated.resources.image_tab_prescription
import medremind.composeapp.generated.resources.image_tab_prescription_description
import medremind.composeapp.generated.resources.no_photo_added_yet
import okio.Path.Companion.toPath
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ImageTabContent(
    prescriptionImagePath: String?,
    medicationImagePath: String?,
    selectedImageType: ImageType,
    onTypeSelected: (ImageType) -> Unit,
    offset: Offset,
    scale: Float,
    onImageTransformChanged: (offset: Offset, scale: Float, containerSize: IntSize) -> Unit,
    modifier: Modifier = Modifier
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)
    
    when (deviceConfiguration) {
        DeviceConfiguration.MOBILE_PORTRAIT,
        DeviceConfiguration.TABLET_PORTRAIT,
        DeviceConfiguration.TABLET_LANDSCAPE,
        DeviceConfiguration.DESKTOP -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ImageTabToggleButtons(
                    selectedImageType = selectedImageType,
                    onTypeSelected = onTypeSelected,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                )

                ImageDisplaySection(
                    prescriptionImagePath = prescriptionImagePath,
                    medicationImagePath = medicationImagePath,
                    selectedImageType = selectedImageType,
                    offset = offset,
                    scale = scale,
                    onImageTransformChanged = onImageTransformChanged
                )
            }
        }
        
        DeviceConfiguration.MOBILE_LANDSCAPE -> {
            Row(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ImageTabToggleButtons(
                    selectedImageType = selectedImageType,
                    onTypeSelected = onTypeSelected
                )

                ImageDisplaySection(
                    prescriptionImagePath = prescriptionImagePath,
                    medicationImagePath = medicationImagePath,
                    selectedImageType = selectedImageType,
                    offset = offset,
                    scale = scale,
                    onImageTransformChanged = onImageTransformChanged
                )
            }
        }
    }
}

@Composable
fun ImageDisplaySection(
    prescriptionImagePath: String?,
    medicationImagePath: String?,
    selectedImageType: ImageType,
    offset: Offset,
    scale: Float,
    onImageTransformChanged: (offset: Offset, scale: Float, containerSize: IntSize) -> Unit,
    modifier: Modifier = Modifier
) {
    val imageToShow = when (selectedImageType) {
        ImageType.PRESCRIPTION -> prescriptionImagePath
        ImageType.MEDICATION -> medicationImagePath
    }
    val imageDescription = when (selectedImageType) {
        ImageType.PRESCRIPTION -> stringResource(Res.string.image_tab_prescription_description)
        ImageType.MEDICATION -> stringResource(Res.string.image_tab_medication_description)
    }
    
    Box(
        modifier = modifier
            .widthIn(max = 500.dp)
            .fillMaxWidth()
            .height(500.dp)
            .background(color = MaterialTheme.colorScheme.surfaceDim),
        contentAlignment = Alignment.Center
    ) {
        if (!imageToShow.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            onImageTransformChanged(pan, zoom, size)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalPlatformContext.current)
                        .data(imageToShow)
                        .crossfade(true)
                        .build(),
                    contentDescription = imageDescription,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .clipToBounds()
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = offset.x
                            translationY = offset.y
                        },
                    loading = {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    error = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.icon_filled_gallery_upload),
                                contentDescription = stringResource(Res.string.failed_to_load),
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(30.dp)
                            )
                            Text(
                                text = stringResource(Res.string.failed_to_load),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.carbonimage),
                    modifier = Modifier.size(48.dp),
                    contentDescription = stringResource(Res.string.icon_no_photo_description),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.no_photo_added_yet),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ImageTabToggleButtons(
    selectedImageType: ImageType,
    onTypeSelected: (ImageType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ToggleButton(
            text = stringResource(Res.string.image_tab_prescription),
            isSelected = selectedImageType == ImageType.PRESCRIPTION,
            onClick = {
                onTypeSelected(ImageType.PRESCRIPTION)
            }
        )
        ToggleButton(
            text = stringResource(Res.string.image_tab_medication),
            isSelected = selectedImageType == ImageType.MEDICATION,
            onClick = {
                onTypeSelected(ImageType.MEDICATION)
            }
        )
    }
}

@Composable
private fun ToggleButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.secondary
    } else MaterialTheme.colorScheme.secondaryFixedDim
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onSecondary
    } else MaterialTheme.colorScheme.onSecondaryFixed
    
    val animatedContainerColor by animateColorAsState(
        targetValue = containerColor,
        animationSpec = tween(durationMillis = 300),
        label = "containerColorAnimation"
    )
    val animatedContentColor by animateColorAsState(
        targetValue = contentColor,
        animationSpec = tween(durationMillis = 300),
        label = "contentColorAnimation"
    )
    val animatedCornerRadius by animateDpAsState(
        targetValue = if (isSelected) 100.dp else 12.dp,
        animationSpec = tween(durationMillis = 300),
        label = "cornerRadiusAnimation"
    )
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scaleAnimation"
    )
    
    Button(
        onClick = onClick,
        modifier = modifier
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            },
        shape = RoundedCornerShape(animatedCornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = animatedContainerColor,
            contentColor = animatedContentColor
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 6.dp)
        )
    }
}

@Preview(
    name = "997dp x 393dp",
    showBackground = true,
    widthDp = 997,
    heightDp = 393
)
@Preview(showBackground = true)
@Composable
private fun ImageTabContentPreview() {
    MaterialTheme {
        ImageTabContent(
            prescriptionImagePath = null,
            medicationImagePath = null,
            selectedImageType = ImageType.PRESCRIPTION,
            onTypeSelected = {},
            offset = Offset.Zero,
            scale = 1f,
            onImageTransformChanged = { _, _, _ -> }
        )
    }
}