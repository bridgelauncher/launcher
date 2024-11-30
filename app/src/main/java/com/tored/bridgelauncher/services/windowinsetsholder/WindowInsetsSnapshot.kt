package com.tored.bridgelauncher.services.windowinsetsholder

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.serialization.Serializable

@Serializable
data class WindowInsetsSnapshot(
    val top: Float,
    val left: Float,
    val right: Float,
    val bottom: Float,
)
{
    companion object
    {
        fun zero() = WindowInsetsSnapshot(0f, 0f, 0f, 0f)
    }
}

fun WindowInsets.getSnapshot(density: Density): WindowInsetsSnapshot
{
    return WindowInsetsSnapshot(
        getLeft(density, LayoutDirection.Ltr) / density.density,
        getTop(density) / density.density,
        getRight(density, LayoutDirection.Ltr) / density.density,
        getBottom(density) / density.density,
    )
}
