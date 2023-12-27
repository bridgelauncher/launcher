package com.tored.bridgelauncher

import android.app.UiModeManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import com.tored.bridgelauncher.ui.screens.home.HomeScreen
import com.tored.bridgelauncher.ui.theme.BridgeLauncherTheme
import com.tored.bridgelauncher.utils.hasStoragePerms
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
        _bridge.hasStoragePerms = hasStoragePerms()
        _modeman = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager

        setContent {
            BridgeLauncherTheme {
                HomeScreen(
                    hasStoragePerms = _bridge.hasStoragePerms
                )
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle)
    {
        Log.d(TAG, "onSaveInstanceState()")
        super.onSaveInstanceState(outState, outPersistentState)
    }

    private var _lastNightModeString: String? = null

    override fun onConfigurationChanged(newConfig: Configuration)
    {
        super.onConfigurationChanged(newConfig)

        val newNightModeString = getSystemNightModeString(_modeman.nightMode)
        if (newNightModeString != _lastNightModeString)
        {
            Log.d(TAG, "onConfigurationChanged: $newNightModeString")
            _bridge.bridgeToJSAPI.systemNightModeChanged(newNightModeString)
            _lastNightModeString = newNightModeString
        }
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

    private var _lastCanSetSystemNightMode: Boolean? = null

    override fun onResume()
    {
        Log.d(TAG, "onResume")
        super.onResume()

        val canSetSystemNightMode = ActivityCompat.checkSelfPermission(this, "android.permission.MODIFY_DAY_NIGHT_MODE") == PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(android.Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED

        if (canSetSystemNightMode != _lastCanSetSystemNightMode)
        {
            _bridge.bridgeToJSAPI.canSetSystemNightModeChanged(canSetSystemNightMode)
            _lastCanSetSystemNightMode = canSetSystemNightMode
        }

        _bridge.hasStoragePerms = hasStoragePerms()
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
