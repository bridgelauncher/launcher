package com.tored.bridgelauncher

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tored.bridgelauncher.ui.screens.home.HomeScreen
import com.tored.bridgelauncher.ui.theme.BridgeLauncherTheme
import com.tored.bridgelauncher.webview.jsapi.getSystemNightModeString
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity()
{
    private lateinit var _bridge: BridgeLauncherApp
    private lateinit var _modeman: UiModeManager

    override fun onCreate(savedInstanceState: Bundle?)
    {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate: savedInstanceState == null: ${savedInstanceState == null}")

        _bridge = applicationContext as BridgeLauncherApp
        _modeman = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager

        setContent {
            BridgeLauncherTheme {
                HomeScreen()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle)
    {
        Log.d(TAG, "onSaveInstanceState()")
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onConfigurationChanged(newConfig: Configuration)
    {
        val newNightModeString = getSystemNightModeString(_modeman.nightMode)
        Log.d(TAG, "onConfigurationChanged: $newNightModeString")
        super.onConfigurationChanged(newConfig)
        _bridge.bridgeToJSAPI.systemNightModeChanged(newNightModeString)
    }

    override fun onStart()
    {
        Log.d(TAG, "onPause")
        super.onStart()
    }

    override fun onPause()
    {
        Log.d(TAG, "onPause")
        _bridge.bridgeToJSAPI.beforePause()
        super.onPause()
    }

    override fun onResume()
    {
        Log.d(TAG, "onResume")
        super.onResume()
        _bridge.bridgeToJSAPI.afterResume()
    }

    override fun onStop()
    {
        Log.d(TAG, "onStop")
        super.onStop()
    }

    override fun onDestroy()
    {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }
}
