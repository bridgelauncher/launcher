package com.tored.bridgelauncher.ui2.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.tored.bridgelauncher.ui.theme.BridgeLauncherTheme
import com.tored.bridgelauncher.ui2.home.composables.HomeScreen2
import com.tored.bridgelauncher.utils.tryStartBridgeSettingsActivity

private val TAG = HomeScreenActivity::class.simpleName

class HomeScreenActivity : ComponentActivity()
{
    private val _homeScreenVM: HomeScreen2VM by viewModels { HomeScreen2VM.Factory }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        tryStartBridgeSettingsActivity()

        setContent {
            BridgeLauncherTheme {
                HomeScreen2(_homeScreenVM)
            }
        }
    }

    override fun onPause()
    {
        _homeScreenVM.beforePause()
        super.onPause()
    }

    override fun onResume()
    {
        super.onResume()
        _homeScreenVM.afterResume()
    }
}