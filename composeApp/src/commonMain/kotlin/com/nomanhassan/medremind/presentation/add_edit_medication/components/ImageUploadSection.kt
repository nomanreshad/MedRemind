@file:OptIn(ExperimentalMaterial3Api::class)

package com.nomanhassan.medremind.presentation.add_edit_medication.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.btn_delete_prescription_image_description
import medremind.composeapp.generated.resources.btn_upload_medication_image_description
import medremind.composeapp.generated.resources.btn_upload_prescription_image_description
import medremind.composeapp.generated.resources.carbonimage
import medremind.composeapp.generated.resources.delete_medication
import medremind.composeapp.generated.resources.delete_prescription
import medremind.composeapp.generated.resources.failed_to_load
import medremind.composeapp.generated.resources.icon_filled_gallery_upload
import medremind.composeapp.generated.resources.icon_outline_rounded_delete
import medremind.composeapp.generated.resources.upload_medication
import medremind.composeapp.generated.resources.upload_prescription
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

data class ImageUploadConfig(
    val imagePath: String?,
    val label: String,
    val deleteLabel: String,
    val contentDescription: String,
    val onUpload: () -> Unit,
    val onDelete: () -> Unit
)

@Composable
fun ImageUploadSection(
    configs: List<ImageUploadConfig>,
    modifier: Modifier = Modifier
) {
    if (configs.isEmpty()) return

    HorizontalCenteredHeroCarousel(
        state = rememberCarouselState { configs.count() },
        modifier = modifier
            .widthIn(max = 500.dp)
            .fillMaxWidth(),
        minSmallItemWidth = 100.dp,
        maxSmallItemWidth = 240.dp,
        itemSpacing = 12.dp,
        contentPadding = PaddingValues(horizontal = 8.dp),
    ) { index ->
        val item = configs[index]

        val isDeleteMode = item.imagePath != null
        val contentColor = if (isDeleteMode) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.primary

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .maskClip(MaterialTheme.shapes.extraLarge)
        ) {
            ImageUploadBox(
                imagePath = item.imagePath,
                contentDescription = item.contentDescription,
                modifier = Modifier
                    .fillMaxWidth()
                    .maskClip(MaterialTheme.shapes.extraLarge)
            )

            OutlinedButton(
                onClick = {
                    if (isDeleteMode) item.onDelete() else item.onUpload()
                },
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isDeleteMode) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = contentColor,
                    containerColor = if (isDeleteMode) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f) else Color.Transparent
                ),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Icon(
                    painter = painterResource(
                        if (isDeleteMode) Res.drawable.icon_outline_rounded_delete
                        else Res.drawable.icon_filled_gallery_upload
                    ),
                    contentDescription = if(isDeleteMode) {
                        stringResource(Res.string.btn_delete_prescription_image_description)
                    } else stringResource(Res.string.btn_upload_prescription_image_description),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (isDeleteMode) item.deleteLabel else item.label,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun ImageUploadBox(
    imagePath: String?,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(width = 240.dp, height = 270.dp)
            .background(color = MaterialTheme.colorScheme.surfaceDim)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (!imagePath.isNullOrBlank()) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(imagePath)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
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
                            painter = painterResource(Res.drawable.carbonimage),
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
        } else {
            Icon(
                painter = painterResource(Res.drawable.carbonimage),
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Preview
@Composable
private fun ImageUploadSectionPrev() {
    MaterialTheme {
        val uploadItems = listOf(
            ImageUploadConfig(
                imagePath = null,
                label = stringResource(Res.string.upload_prescription),
                deleteLabel = stringResource(Res.string.delete_prescription),
                contentDescription = stringResource(Res.string.btn_upload_prescription_image_description),
                onUpload = {},
                onDelete = {}
            ),
            ImageUploadConfig(
                imagePath = "",
                label = stringResource(Res.string.upload_medication),
                deleteLabel = stringResource(Res.string.delete_medication),
                contentDescription = stringResource(Res.string.btn_upload_medication_image_description),
                onUpload = {},
                onDelete = {}
            )
        )

        ImageUploadSection(configs = uploadItems)
    }
}