package com.tored.bridgelauncher.services.windowinsetsholder

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
import com.tored.bridgelauncher.utils.RawRepresentable

typealias WindowInsetsGetter = @Composable WindowInsets.Companion.() -> WindowInsets

@OptIn(ExperimentalLayoutApi::class)
enum class WindowInsetsOptions(override val rawValue: String, val getter: WindowInsetsGetter) : RawRepresentable<String>
{
    StatusBars("statusBars",  { statusBars }),
    StatusBarsIgnoringVisibility("statusBarsIgnoringVisibility",  { statusBarsIgnoringVisibility }),
    NavigationBars("navigationBars",  { navigationBars }),
    NavigationBarsIgnoringVisibility("navigationBarsIgnoringVisibility",  { navigationBarsIgnoringVisibility }),
    CaptionBar("captionBar",  { captionBar }),
    CaptionBarIgnoringVisibility("captionBarIgnoringVisibility",  { captionBarIgnoringVisibility }),
    SystemBars("systemBars",  { systemBars }),
    SystemBarsIgnoringVisibility("systemBarsIgnoringVisibility",  { systemBarsIgnoringVisibility }),
    Ime("ime",  { ime }),
    ImeAnimationSource("imeAnimationSource",  { imeAnimationSource }),
    ImeAnimationTarget("imeAnimationTarget",  { imeAnimationTarget }),
    TappableElement("tappableElement",  { tappableElement }),
    TappableElementIgnoringVisibility("tappableElementIgnoringVisibility",  { tappableElementIgnoringVisibility }),
    SystemGestures("systemGestures",  { systemGestures }),
    MandatorySystemGestures("mandatorySystemGestures",  { mandatorySystemGestures }),
    DisplayCutout("displayCutout",  { displayCutout }),
    Waterfall("waterfall",  { waterfall }),
}
