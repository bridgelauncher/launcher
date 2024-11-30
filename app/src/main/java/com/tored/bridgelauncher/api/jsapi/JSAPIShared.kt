package com.tored.bridgelauncher.api.jsapi

import android.app.UiModeManager
import com.tored.bridgelauncher.services.settings2.SystemBarAppearanceOptions
import com.tored.bridgelauncher.services.settings2.BridgeThemeOptions
import com.tored.bridgelauncher.utils.RawRepresentable
import com.tored.bridgelauncher.utils.q
import com.tored.bridgelauncher.utils.serialization.StringEnumWriteOnlySerializer
import kotlinx.serialization.Serializable

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = StringEnumWriteOnlySerializer::class)
enum class BridgeButtonVisibilityOptions(override val rawValue: String) : RawRepresentable<String>
{
    Shown("shown"),
    Hidden("hidden"),
}

fun getBridgeButtonVisiblityString(showBridgeButton: Boolean): String
{
    return when (showBridgeButton)
    {
        true -> BridgeButtonVisibilityOptions.Shown.rawValue
        false -> BridgeButtonVisibilityOptions.Hidden.rawValue
    }
}


enum class OverscrollEffects(val value: String)
{
    Default("default"),
    None("none"),
}

fun getOverscrollEffects(draw: Boolean): String
{
    return when (draw)
    {
        true -> OverscrollEffects.Default.value
        false -> OverscrollEffects.None.value
    }
}

fun getBridgeThemeString(theme: BridgeThemeOptions): String
{
    return when (theme)
    {
        BridgeThemeOptions.System -> "system"
        BridgeThemeOptions.Light -> "light"
        BridgeThemeOptions.Dark -> "dark"
    }
}

fun getSystemBarAppearanceString(appearance: SystemBarAppearanceOptions): String
{
    return when (appearance)
    {
        SystemBarAppearanceOptions.Hide -> "hide"
        SystemBarAppearanceOptions.LightIcons -> "light-fg"
        SystemBarAppearanceOptions.DarkIcons -> "dark-fg"
    }
}

fun stringToSystemBarAppearance(appearance: String): SystemBarAppearanceOptions
{
    return when (appearance)
    {
        "hide" -> SystemBarAppearanceOptions.Hide
        "light-fg" -> SystemBarAppearanceOptions.LightIcons
        "dark-fg" -> SystemBarAppearanceOptions.DarkIcons
        else -> throw Exception("Appearance must be one of ${q("hide")}, ${q("light-fg")} or ${"dark-fg"} (got ${q(appearance)}).")
    }
}

fun getSystemNightModeString(nightMode: Int): String
{
    return when (nightMode)
    {
        UiModeManager.MODE_NIGHT_NO -> "no"
        UiModeManager.MODE_NIGHT_YES -> "yes"
        UiModeManager.MODE_NIGHT_AUTO -> "auto"
        UiModeManager.MODE_NIGHT_CUSTOM -> "custom"
        -1 -> "error"
        else -> "unknown"
    }
}