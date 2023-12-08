package com.tored.bridgelauncher

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tored.bridgelauncher.ui.screens.home.HomeScreen
import com.tored.bridgelauncher.ui.theme.BridgeLauncherTheme
import com.tored.bridgelauncher.webview.jsapi.getSystemNightModeString
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity()
{
    private lateinit var _bridge: BridgeLauncherApp
    private lateinit var _modeman: UiModeManager

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        _bridge = applicationContext as BridgeLauncherApp
        _modeman = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager

        setContent {
            BridgeLauncherTheme {
                HomeScreen()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration)
    {
        super.onConfigurationChanged(newConfig)
        _bridge.bridgeToJSAPI.systemNightModeChanged(getSystemNightModeString(_modeman.nightMode))
    }

    override fun onPause()
    {
        _bridge.bridgeToJSAPI.beforePause()
        super.onPause()
    }

    override fun onResume()
    {
        super.onResume()
        _bridge.bridgeToJSAPI.afterResume()
    }
}
