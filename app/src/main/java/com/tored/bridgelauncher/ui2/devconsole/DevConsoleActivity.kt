package com.tored.bridgelauncher.ui2.devconsole

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.tored.bridgelauncher.ui.theme.BridgeLauncherTheme
import com.tored.bridgelauncher.ui2.devconsole.composables.DevConsoleScreen

class DevConsoleActivity : ComponentActivity()
{
    private val _devConsoleVM: DevConsoleVM by viewModels { DevConsoleVM.Factory }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setContent {
            BridgeLauncherTheme {
                DevConsoleScreen(
                    vm = _devConsoleVM,
                    requestFinish = { finish() }
                )
            }
        }
    }
}
