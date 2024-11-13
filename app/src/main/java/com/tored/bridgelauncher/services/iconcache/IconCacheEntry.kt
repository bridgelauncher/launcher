package com.tored.bridgelauncher.services.iconcache

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.Deferred

data class IconCacheEntry(
    val generationStartTimeNano: Long,
    val iconGenerationDeferred: Deferred<ImageBitmap>,
)