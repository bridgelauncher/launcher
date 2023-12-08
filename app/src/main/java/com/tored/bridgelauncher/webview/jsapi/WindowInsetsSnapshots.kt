package com.tored.bridgelauncher.webview.jsapi

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.captionBarIgnoringVisibility
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imeAnimationSource
import androidx.compose.foundation.layout.imeAnimationTarget
import androidx.compose.foundation.layout.mandatorySystemGestures
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsIgnoringVisibility
import androidx.compose.foundation.layout.systemGestures
import androidx.compose.foundation.layout.tappableElement
import androidx.compose.foundation.layout.tappableElementIgnoringVisibility
import androidx.compose.foundation.layout.waterfall
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

typealias WindowInsetsForJS = WindowInsetsSnapshot

@Serializable
data class WindowInsetsSnapshot(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
)
{
    fun toJson() = Json.encodeToString(serializer(), this)
}

fun defaultInsets() = WindowInsetsSnapshot(0f, 0f, 0f, 0f)

class WindowInsetsSnapshots(
    val statusBars: WindowInsetsForJS = defaultInsets(),
    val statusBarsIgnoringVisibility: WindowInsetsForJS = defaultInsets(),

    val navigationBars: WindowInsetsForJS = defaultInsets(),
    val navigationBarsIgnoringVisibility: WindowInsetsForJS = defaultInsets(),

    val captionBar: WindowInsetsForJS = defaultInsets(),
    val captionBarIgnoringVisibility: WindowInsetsForJS = defaultInsets(),

    val systemBars: WindowInsetsForJS = defaultInsets(),
    val systemBarsIgnoringVisibility: WindowInsetsForJS = defaultInsets(),

    val ime: WindowInsetsForJS = defaultInsets(),
    val imeAnimationSource: WindowInsetsForJS = defaultInsets(),
    val imeAnimationTarget: WindowInsetsForJS = defaultInsets(),

    val tappableElement: WindowInsetsForJS = defaultInsets(),
    val tappableElementIgnoringVisibility: WindowInsetsForJS = defaultInsets(),

    val systemGestures: WindowInsetsForJS = defaultInsets(),
    val mandatorySystemGestures: WindowInsetsForJS = defaultInsets(),

    val displayCutout: WindowInsetsForJS = defaultInsets(),
    val waterfall: WindowInsetsForJS = defaultInsets(),
)
{
    companion object
    {
        @OptIn(ExperimentalLayoutApi::class)
        @Composable
        fun compose(): WindowInsetsSnapshots
        {
            with(LocalDensity.current)
            {
                return WindowInsetsSnapshots(
                    statusBars = snapshot(WindowInsets.statusBars),
                    statusBarsIgnoringVisibility = snapshot(WindowInsets.statusBarsIgnoringVisibility),

                    navigationBars = snapshot(WindowInsets.navigationBars),
                    navigationBarsIgnoringVisibility = snapshot(WindowInsets.navigationBarsIgnoringVisibility),

                    captionBar = snapshot(WindowInsets.captionBar),
                    captionBarIgnoringVisibility = snapshot(WindowInsets.captionBarIgnoringVisibility),

                    systemBars = snapshot(WindowInsets.systemBars),
                    systemBarsIgnoringVisibility = snapshot(WindowInsets.systemBarsIgnoringVisibility),

                    ime = snapshot(WindowInsets.ime),
                    imeAnimationSource = snapshot(WindowInsets.imeAnimationSource),
                    imeAnimationTarget = snapshot(WindowInsets.imeAnimationTarget),

                    tappableElement = snapshot(WindowInsets.tappableElement),
                    tappableElementIgnoringVisibility = snapshot(WindowInsets.tappableElementIgnoringVisibility),

                    systemGestures = snapshot(WindowInsets.systemGestures),
                    mandatorySystemGestures = snapshot(WindowInsets.mandatorySystemGestures),

                    displayCutout = snapshot(WindowInsets.displayCutout),
                    waterfall = snapshot(WindowInsets.waterfall),
                )
            }
        }
    }
}

fun Density.snapshot(insets: WindowInsets): WindowInsetsSnapshot
{
    return WindowInsetsSnapshot(
        insets.getLeft(this, LayoutDirection.Ltr) / this.density,
        insets.getTop(this) / this.density,
        insets.getRight(this, LayoutDirection.Ltr) / this.density,
        insets.getBottom(this) / this.density,
    )
}