package com.tored.bridgelauncher.webview.jsapi

import android.content.Context
import android.webkit.JavascriptInterface
import android.widget.Toast

data class InstalledAppInfo(
    val uid: Int,
    val packageName: String,
    val label: String,
    val labelNormalized: String,
)

class JSToBridgeAPI(
    private val _context: Context,
) : Any()
{
    // apps
    @JavascriptInterface
    fun getInstalledApps(): Array<InstalledAppInfo>
    {
        return arrayOf<InstalledAppInfo>()
    }

    @JavascriptInterface
    fun requestAppUninstall(packageName: String)
    {

    }

    @JavascriptInterface
    fun launchApp(packageName: String)
    {


    }

    // wallpaper offsets
    @JavascriptInterface
    fun setWallpaperOffsetSteps(x: Float, y: Float)
    {


    }

    @JavascriptInterface
    fun setWallpaperOffsets(x: Float, y: Float)
    {


    }

    // bridge button
    @JavascriptInterface
    fun getBridgeButtonVisibility(): String
    {
        return ""
    }

    @JavascriptInterface
    fun setBridgeButtonVisibility(state: String)
    {


    }

    // draw system wallpaper behind webview
    @JavascriptInterface
    fun getDrawSystemWallpaperBehindWebViewEnabled(): Boolean
    {
        return false
    }

    @JavascriptInterface
    fun setDrawSystemWallpaperBehindWebViewEnabled(enable: Boolean)
    {


    }

    // system theme
    @JavascriptInterface
    fun getIsSystemInDarkTheme(): Boolean
    {
        return false
    }

    @JavascriptInterface
    fun setIsSystemInDarkTheme(shouldBeInDarkTheme: Boolean)
    {


    }

    // Bridge theme
    @JavascriptInterface
    fun getBridgeTheme(): String
    {
        return ""
    }

    @JavascriptInterface
    fun setBridgeTheme(theme: String)
    {


    }

    // status bar
    @JavascriptInterface
    fun getStatusBarAppearance(): String
    {
        return ""
    }

    @JavascriptInterface
    fun setStatusBarAppearance(appearance: String)
    {

    }

    @JavascriptInterface
    fun getStatusBarHeight(): Float
    {
        return 0f
    }

    // navigation bar
    @JavascriptInterface
    fun getNavigationBarAppearance(): String
    {
        return ""
    }

    @JavascriptInterface
    fun setNavigationBarAppearance(appearance: String)
    {

    }

    @JavascriptInterface
    fun getNavigationBarHeight(): Float
    {
        return 0f
    }

    // screen locking
    @JavascriptInterface
    fun getCanLockScreen(): Boolean
    {
        return true
    }

    @JavascriptInterface
    fun requestLockScreen(quiet: Boolean): Boolean
    {
        return false
    }

    // misc requests
    @JavascriptInterface
    fun requestOpenBridgeSettings()
    {


    }

    @JavascriptInterface
    fun requestOpenDeveloperConsole()
    {

    }

    @JavascriptInterface
    fun requestExpandNotificationShade()
    {

    }

    @JavascriptInterface
    fun requestChangeSystemWallpaper()
    {

    }

    // toast
    @JavascriptInterface
    fun showToast(message: String, long: Boolean = false)
    {
        Toast.makeText(_context, message, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
    }
}