package com.tored.bridgelauncher.services.settings2

import com.tored.bridgelauncher.services.settings.SystemBarAppearanceOptions
import com.tored.bridgelauncher.services.settings.ThemeOptions

object BridgeSettings
{
    val isQSTileAdded = BridgeSetting.systemBool("isQSTileAdded")
    val isDeviceAdminEnabled = BridgeSetting.systemBool("isDeviceAdminEnabled")
    val isAccessibilityServiceEnabled = BridgeSetting.systemBool("isAccessibilityServiceEnabled")
    val isExternalStorageManager = BridgeSetting.systemBool("isExternalStorageManager")

    val currentProjDir = BridgeSetting.file("currentProjDir")
    val lastMockExportDir = BridgeSetting.file("lastMockExportDir")

    val theme = BridgeSetting.enum(
        key = "theme",
        defaultValue = ThemeOptions.System,
    )

    val allowProjectsToTurnScreenOff = BridgeSetting.bool(
        key = "allowProjectsToTurnScreenOff",
        displayName = "Allow projects to turn the screen off",
    )

    val drawSystemWallpaperBehindWebView = BridgeSetting.bool(
        key = "Draw system wallpaper behind WebView",
        defaultValue = true,
        displayName = "Draw system wallpaper behind WebView",
    )

    val statusBarAppearance = BridgeSetting.enum(
        key = "statusBarAppearance",
        defaultValue = SystemBarAppearanceOptions.DarkIcons,
        displayName = "Status bar",
    )

    val navigationBarAppearance = BridgeSetting.enum(
        key = "navigationBarAppearance",
        defaultValue = SystemBarAppearanceOptions.DarkIcons,
        displayName = "Navigation bar",
    )

    val drawWebViewOverscrollEffects = BridgeSetting.bool(
        key = "drawWebViewOverscrollEffects",
        displayName = "Draw WebView overscroll effects",
    )

    val showBridgeButton = BridgeSetting.bool(
        key = "showBridgeButton",
        defaultValue = true,
        displayName = "Show Bridge button",
    )

    val showLaunchAppsWhenBridgeButtonCollapsed = BridgeSetting.bool(
        key = "showBridgeButton",
        displayName = "Show \"Launch apps\" button when the Bridge menu is collapsed",
    )
}