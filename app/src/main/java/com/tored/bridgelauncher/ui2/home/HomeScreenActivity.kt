package com.tored.bridgelauncher.ui2.home

import android.app.UiModeManager
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.tored.bridgelauncher.ui2.home.composables.HomeScreen2
import com.tored.bridgelauncher.ui2.theme.BridgeLauncherTheme

private val TAG = HomeScreenActivity::class.simpleName

class HomeScreenActivity : ComponentActivity()
{
    private lateinit var _modeman: UiModeManager

    private val _homeScreenVM: HomeScreen2VM by viewModels { HomeScreen2VM.Factory }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        _modeman = getSystemService(UI_MODE_SERVICE) as UiModeManager

        _homeScreenVM.afterCreate(this)

        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        // immediately start another activity for debugging
//        tryStartBridgeAppDrawerActivity()
//        tryStartBridgeSettingsActivity()
//        tryStartDevConsoleActivity()

        setContent {
            BridgeLauncherTheme {
                HomeScreen2(_homeScreenVM)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration)
    {
        super.onConfigurationChanged(newConfig)
        _homeScreenVM.onConfigurationChanged()
    }

    override fun onPause()
    {
        _homeScreenVM.beforePause()
        super.onPause()
    }

    override fun onNewIntent(intent: Intent)
    {
        super.onNewIntent(intent)
        _homeScreenVM.onNewIntent()
    }

    override fun onResume()
    {
        super.onResume()
        _homeScreenVM.afterResume()
    }

    override fun onDestroy()
    {
        _homeScreenVM.beforeDestroy()
        super.onDestroy()
    }
}