package com.tored.bridgelauncher.ui2.home.bridgemenu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.composables.ResIcon
import com.tored.bridgelauncher.ui.theme.BridgeLauncherThemeStateless
import com.tored.bridgelauncher.ui2.home.bridgemenu.IBridgeMenuElement.Button
import com.tored.bridgelauncher.ui2.home.bridgemenu.IBridgeMenuElement.Divider
import com.tored.bridgelauncher.utils.addAll
import com.tored.bridgelauncher.utils.tryStartAndroidHomeSettingsActivity
import com.tored.bridgelauncher.utils.tryStartBridgeAppDrawerActivity
import com.tored.bridgelauncher.utils.tryStartBridgeSettingsActivity
import com.tored.bridgelauncher.utils.tryStartDevConsoleActivity

@Composable
fun BridgeMenu(
    state: BridgeMenuState,
    actions: BridgeMenuActions,
    modifier: Modifier = Modifier,
)
{
    if (!state.isShown) return

    val entriesToDisplay = mutableListOf<IBridgeMenuElement>()
    val context = LocalContext.current

    if (state.isExpanded)
    {
        entriesToDisplay.addAll(
            Button(R.drawable.ic_refresh, "Refresh WebView", actions.onWebViewRefreshRequest),
            Button(R.drawable.ic_dev_console, "Developer console") { context.tryStartDevConsoleActivity() },
            Divider,
            Button(R.drawable.ic_switch_launchers, "Switch away from Bridge") { context.tryStartAndroidHomeSettingsActivity() },
            Button(R.drawable.ic_settings, "Bridge settings") { context.tryStartBridgeSettingsActivity() },
            Button(R.drawable.ic_hide, "Hide Bridge button", actions.onHideBridgeButtonRequest),
        )
    }

    if (state.isExpanded || state.showAppDrawerButtonWhenCollapsed)
    {
        entriesToDisplay.addAll(
            Button(R.drawable.ic_apps, "Built-in app drawer") { context.tryStartBridgeAppDrawerActivity() },
            Divider,
        )
    }

    entriesToDisplay.addAll(
        Button(
            R.drawable.ic_bridge,
            if (state.isExpanded) "Collapse this menu" else "Expand the Bridge Menu"
        )
        {
            actions.onRequestIsExpandedChange(!state.isExpanded)
        },
    )

    Row(
        modifier = modifier
            .wrapContentSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    )
    {
        // label column
        if (state.isExpanded)
        {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
            )
            {
                for (entry in entriesToDisplay)
                {
                    if (entry is Button)
                    {
                        BridgeMenuButtonLabel(entry)
                    }
                    else
                    {
                        assert(entry is Divider)
                        Spacer(Modifier.size(1.dp))
                    }
                }
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
                for (entry in entriesToDisplay)
                {
                    if (entry is Button)
                    {
                        BridgeMenuButton(entry)
                    }
                    else
                    {
                        assert(entry is Divider)
                        BridgeMenuDivider()
                    }
                }
            }

        }
    }
}

@Composable
fun BridgeMenuWithEmptyActions(state: BridgeMenuState) = BridgeMenu(
    state = state,
    actions = BridgeMenuActions.empty(),
)

@Composable
fun BridgeMenuDivider()
{
    androidx.compose.material.Divider()
}

@Composable
fun BridgeMenuButtonLabel(button: Button)
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
                text = button.text,
                modifier = Modifier
                    .padding(16.dp, 8.dp),
            )
        }
    }
}

@Composable
fun BridgeMenuButton(button: Button)
{
    Box(
        modifier = Modifier
            .clickable(onClick = button.action)
            .size(56.dp),
        contentAlignment = Alignment.Center,
    )
    {
        ResIcon(iconResId = button.iconResId)
    }
}

@Preview
@Composable
fun BridgeMenuPreviewFullyCollapsed()
{
    BridgeLauncherThemeStateless(useDarkTheme = false) {
        BridgeMenuWithEmptyActions(
            BridgeMenuState(
                isShown = true,
                isExpanded = false,
                showAppDrawerButtonWhenCollapsed = false
            )
        )
    }
}

@Preview
@Composable
fun BridgeMenuPreviewCollapsedWithAppDrawerButton()
{
    BridgeLauncherThemeStateless(useDarkTheme = false) {
        BridgeMenuWithEmptyActions(
            BridgeMenuState(
                isShown = true,
                isExpanded = false,
                showAppDrawerButtonWhenCollapsed = true
            )
        )
    }
}

@Preview
@Composable
fun BridgeMenuPreviewExpandedLight()
{
    BridgeLauncherThemeStateless(useDarkTheme = false) {
        BridgeMenuWithEmptyActions(
            BridgeMenuState(
                isShown = true,
                isExpanded = true,
                showAppDrawerButtonWhenCollapsed = false
            )
        )
    }
}

@Preview
@Composable
fun BridgeMenuPreviewExpandedDark()
{
    BridgeLauncherThemeStateless(useDarkTheme = true) {
        BridgeMenuWithEmptyActions(
            BridgeMenuState(
                isShown = true,
                isExpanded = true,
                showAppDrawerButtonWhenCollapsed = false
            )
        )
    }
}