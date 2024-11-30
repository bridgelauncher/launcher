package com.tored.bridgelauncher.ui2.settings.sections.bridge

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.ui2.shared.Btn
import com.tored.bridgelauncher.ui2.shared.ResIcon
import com.tored.bridgelauncher.services.settings2.BridgeSettings
import com.tored.bridgelauncher.services.settings2.BridgeThemeOptions
import com.tored.bridgelauncher.ui2.shared.ActionCard
import com.tored.bridgelauncher.ui2.shared.CheckboxField
import com.tored.bridgelauncher.ui2.shared.OptionsRow
import com.tored.bridgelauncher.ui2.shared.PreviewWithSurfaceAndPadding
import com.tored.bridgelauncher.ui2.theme.borders

@Composable
fun SettingsScreen2BridgeSectionContent(
    state: SettingsScreen2BridgeSectionState,
    actions: SettingsScreen2BridgeSectionActions,
    modifier: Modifier = Modifier
)
{
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier,
    )
    {
        OptionsRow(
            label = "Theme",
            options = mapOf(
                BridgeThemeOptions.System to "System",
                BridgeThemeOptions.Light to "Light",
                BridgeThemeOptions.Dark to "Dark",
            ),
            selectedOption = state.theme,
            onChange = { actions.changeTheme(it) },
//        onChange = { theme ->
//            vm.edit {
//                setBridgeSetting(BridgeSettings.theme, theme)
//            }
//        },
        )

        CheckboxField(
            label = BridgeSettings.showBridgeButton.displayName,
            isChecked = state.showBridgeButton,
            onCheckedChange = { actions.changeShowBridgeButton(it) }
        )

//                    ProvideTextStyle(value = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.textSec))
//                    {
//                        Tip("Tap and hold the button to move it.")
//                    }


        CheckboxField(
            label = BridgeSettings.showLaunchAppsWhenBridgeButtonCollapsed.displayName,
            isChecked = state.showLaunchAppsWhenBridgeButtonCollapsed,
            onCheckedChange = { actions.changeShowLaunchAppsWhenBridgeButtonCollapsed(it) }
        )

        if (!state.isQSTileAdded)
        {
//        if (CurrentAndroidVersion.supportsQSTilePrompt())
            if (state.isQSTilePromptSupported)
            {
                ActionCard(
                    title = "Quick settings tile",
                    description = "You can add a quick settings tile to unobtrusively toggle the Bridge button. Long-pressing the tile opens this settings screen."
                )
                {
//                val sbm = context.getSystemService(StatusBarManager::class.java)
//                val compName = ComponentName(context, BridgeButtonQSTileService::class.java)

                    Btn(
                        text = "Add tile",
                        suffixIcon = R.drawable.ic_plus,
                        onClick = { actions.requestQSTilePrompt() },
//                    onClick = {
//                    sbm.requestAddTileService(
//                        compName,
//                        "Bridge button",
//                        Icon.createWithResource(context, R.drawable.ic_bridge_white),
//                        {},
//                        {}
                    )
                }
            }
            else
            {
                ActionCard(
                    title = "Quick settings tile",
                    description = "You can add a quick settings tile to unobtrusively toggle the Bridge button. Long-pressing the tile opens this settings screen.\n"
                            + "\n"
                            + "Quick settings are the toggles in your notifications area that you use to toggle for example WiFi or Bluetooth. "
                            + "To add the Bridge button tile, expand the quick settings panel and look for an edit button."
                )
            }
        }
        else
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .border(MaterialTheme.borders.soft, MaterialTheme.shapes.medium)
                    .padding(start = 12.dp, top = 16.dp, bottom = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            )
            {
                ResIcon(R.drawable.ic_check, color = MaterialTheme.colors.primary)
                Text("Quick settings tile added.")
            }
        }
    }
}


// PREVIEWS

@Composable
fun SettingsScreen2BridgeSectionPreview(
    theme: BridgeThemeOptions = BridgeThemeOptions.System,
    showBridgeButton: Boolean = true,
    showLaunchAppsWhenBridgeButtonCollapsed: Boolean = true,
    isQSTileAdded: Boolean = false,
    isQSTilePromptSupported: Boolean = false,
)
{
    PreviewWithSurfaceAndPadding {
        SettingsScreen2BridgeSectionContent(
            state = SettingsScreen2BridgeSectionState(
                theme = theme,
                showBridgeButton = showBridgeButton,
                showLaunchAppsWhenBridgeButtonCollapsed = showLaunchAppsWhenBridgeButtonCollapsed,
                isQSTileAdded = isQSTileAdded,
                isQSTilePromptSupported = isQSTilePromptSupported,
            ),
            actions = SettingsScreen2BridgeSectionActions.empty(),
        )
    }
}

@Composable
@PreviewLightDark
fun SettingsScreen2BridgeSectionPreview01()
{
    SettingsScreen2BridgeSectionPreview(
        isQSTilePromptSupported = false
    )
}

@Composable
@PreviewLightDark
fun SettingsScreen2BridgeSectionPreview02()
{
    SettingsScreen2BridgeSectionPreview(
        theme = BridgeThemeOptions.Light,
        showBridgeButton = false,
        isQSTilePromptSupported = true,
    )
}

@Composable
@PreviewLightDark
fun SettingsScreen2BridgeSectionPreview03()
{
    SettingsScreen2BridgeSectionPreview(
        theme = BridgeThemeOptions.Dark,
        showBridgeButton = true,
        showLaunchAppsWhenBridgeButtonCollapsed = false,
        isQSTileAdded = true,
    )
}