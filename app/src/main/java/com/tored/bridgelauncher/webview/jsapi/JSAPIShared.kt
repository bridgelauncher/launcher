package com.tored.bridgelauncher.webview.jsapi

import android.app.UiModeManager
import com.tored.bridgelauncher.settings.SystemBarAppearanceOptions
import com.tored.bridgelauncher.settings.ThemeOptions
import com.tored.bridgelauncher.utils.q

enum class BridgeButtonVisibility(val value: String)
{
    Shown("shown"),
    Hidden("hidden"),
}

fun getBridgeButtonVisiblityString(showBridgeButton: Boolean): String
{
    return when (showBridgeButton)
    {
        true -> BridgeButtonVisibility.Shown.value
        false -> BridgeButtonVisibility.Hidden.value
    }
}

fun getBridgeThemeString(theme: ThemeOptions): String
{
    return when (theme)
    {
        ThemeOptions.System -> "system"
        ThemeOptions.Light -> "light"
        ThemeOptions.Dark -> "dark"
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