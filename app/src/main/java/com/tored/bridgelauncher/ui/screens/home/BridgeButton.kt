package com.tored.bridgelauncher.ui.screens.home

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tored.bridgelauncher.AppDrawerActivity
import com.tored.bridgelauncher.DevConsoleActivity
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.SettingsActivity
import com.tored.bridgelauncher.composables.ResIcon
import com.tored.bridgelauncher.settings.SettingsState
import com.tored.bridgelauncher.settings.SettingsVM
import com.tored.bridgelauncher.utils.writeBool

@Composable
fun BridgeButtonStateless(
    isExpanded: Boolean,
    onIsExpandedChange: (newState: Boolean) -> Unit,
    onWebViewRefreshRequest: () -> Unit,
    settingsVM: SettingsVM = viewModel(),
)
{
    val settingsState by settingsVM.settingsUIState.collectAsStateWithLifecycle()

    Row(
        modifier = Modifier
            .wrapContentSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    )
    {
        // label column
        if (isExpanded)
        {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
            )
            {
                TouchTargetLabel(text = "Refresh WebView")
                TouchTargetLabel(text = "Developer console")

                Divider(
                    modifier = Modifier.width(56.dp),
                    color = Color.Transparent,
                )

                TouchTargetLabel(text = "Switch away from Bridge")
                TouchTargetLabel(text = "Bridge settings")
                TouchTargetLabel(text = "Hide Bridge button")
                TouchTargetLabel(text = "Built-in app drawer")

                Divider(
                    modifier = Modifier.width(56.dp),
                    color = Color.Transparent,
                )

                TouchTargetLabel(text = "Collapse this menu")
            }
        }

        Surface(
            modifier = Modifier
                .wrapContentSize(),
            color = MaterialTheme.colors.surface,
            shape = MaterialTheme.shapes.large,
            elevation = 4.dp,
        )
        {
            val context = LocalContext.current

            // button column
            Column(
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.End,
            )
            {
                if (isExpanded)
                {
                    TouchTarget(iconResId = R.drawable.ic_refresh)
                    {
                        onWebViewRefreshRequest()
                    }

                    TouchTarget(iconResId = R.drawable.ic_dev_console)
                    {
                        context.startActivity(Intent(context, DevConsoleActivity::class.java))
                    }

                    Divider()

                    TouchTarget(iconResId = R.drawable.ic_switch_launchers)
                    {
                        context.startActivity(
                            Intent(Settings.ACTION_HOME_SETTINGS).apply()
                            {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                        )
                    }

                    TouchTarget(iconResId = R.drawable.ic_settings)
                    {
                        context.startActivity(Intent(context, SettingsActivity::class.java))
                    }

                    TouchTarget(iconResId = R.drawable.ic_hide)
                    {
                        settingsVM.edit {
                            writeBool(SettingsState::showBridgeButton, false)
                        }
                    }
                }

                if (isExpanded || settingsState.showLaunchAppsWhenBridgeButtonCollapsed)
                {
                    TouchTarget(iconResId = R.drawable.ic_apps)
                    {
                        context.startActivity(Intent(context, AppDrawerActivity::class.java))
                    }

                    Divider()
                }

                TouchTarget(iconResId = R.drawable.ic_bridge)
                {
                    onIsExpandedChange(!isExpanded)
                }
            }

        }
    }
}

@Composable
fun TouchTarget(iconResId: Int, onClick: () -> Unit)
{
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .size(56.dp),
        contentAlignment = Alignment.Center,
    )
    {
        ResIcon(iconResId = iconResId)
    }
}

@Composable
fun TouchTargetLabel(text: String)
{
    Box(
        modifier = Modifier
            .height(56.dp)
            .wrapContentWidth(),
        contentAlignment = Alignment.CenterEnd,
    )
    {
        Surface(
            shape = MaterialTheme.shapes.small,
            elevation = 4.dp
        )
        {
            Text(
                text = text,
                modifier = Modifier
                    .padding(16.dp, 8.dp),
            )
        }
    }
}