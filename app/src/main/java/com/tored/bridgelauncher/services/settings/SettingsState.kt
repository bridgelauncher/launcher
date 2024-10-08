package com.tored.bridgelauncher.services.settings

import android.os.Build
import android.os.Build.VERSION_CODES
import com.tored.bridgelauncher.annotations.Display
import com.tored.bridgelauncher.annotations.DontReset
import com.tored.bridgelauncher.ui.dirpicker.Directory
import com.tored.bridgelauncher.utils.RawRepresentable

enum class SystemBarAppearanceOptions(override val rawValue: Int) : RawRepresentable<Int>
{
    Hide(0),
    LightIcons(1),
    DarkIcons(2),
}

enum class ThemeOptions(override val rawValue: Int) : RawRepresentable<Int>
{
    System(0),
    Light(1),
    Dark(2),
}

data class SettingsState(

    @DontReset
    val isQSTileAdded: Boolean = false,
    @DontReset
    val isDeviceAdminEnabled: Boolean = false,
    @DontReset
    val isAccessibilityServiceEnabled: Boolean = false,
    @DontReset
    val isExternalStorageManager: Boolean = false,

    val currentProjDir: Directory? = null,
    val lastMockExportDir: Directory? = null,

    val theme: ThemeOptions = ThemeOptions.System,

    @Display("Allow projects to turn the screen off")
    val allowProjectsToTurnScreenOff: Boolean = false,

    @Display("Draw system wallpaper behind WebView")
    val drawSystemWallpaperBehindWebView: Boolean = true,

    @Display("Status bar")
    val statusBarAppearance: SystemBarAppearanceOptions = SystemBarAppearanceOptions.DarkIcons,

    @Display("Navigation bar")
    val navigationBarAppearance: SystemBarAppearanceOptions = SystemBarAppearanceOptions.DarkIcons,

    @Display("Draw WebView overscroll effects")
    val drawWebViewOverscrollEffects: Boolean = false,

    @Display("Show Bridge button")
    val showBridgeButton: Boolean = true,

    @Display("Show Launch apps button when the Bridge menu is collapsed")
    val showLaunchAppsWhenBridgeButtonCollapsed: Boolean = false,
)

fun SettingsState.getCanLockScreen(): Boolean
{
    return (
            if (Build.VERSION.SDK_INT >= VERSION_CODES.P)
                isAccessibilityServiceEnabled
            else
                isDeviceAdminEnabled
            )
            && allowProjectsToTurnScreenOff
}