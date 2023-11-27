package com.tored.bridgelauncher.ui.screens.settings.sections

import android.app.StatusBarManager
import android.content.ComponentName
import android.graphics.drawable.Icon
import android.os.Build
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.composables.Btn
import com.tored.bridgelauncher.composables.ResIcon
import com.tored.bridgelauncher.services.BridgeButtonQSTileService
import com.tored.bridgelauncher.settings.SettingsState
import com.tored.bridgelauncher.settings.SettingsVM
import com.tored.bridgelauncher.settings.ThemeOptions
import com.tored.bridgelauncher.ui.screens.settings.SettingsCheckboxFieldFor
import com.tored.bridgelauncher.ui.screens.settings.SettingsSection
import com.tored.bridgelauncher.ui.shared.ActionCard
import com.tored.bridgelauncher.ui.shared.OptionsRow
import com.tored.bridgelauncher.ui.theme.borders
import com.tored.bridgelauncher.utils.writeEnum

@Composable
fun SettingsBridgeSection(vm: SettingsVM = viewModel())
{
    val uiState by vm.settingsUIState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    SettingsSection(label = "Bridge", iconResId = R.drawable.ic_bridge)
    {
        OptionsRow(
            label = "Theme",
            options = mapOf(
                ThemeOptions.System to "System",
                ThemeOptions.Light to "Light",
                ThemeOptions.Dark to "Dark",
            ),
            selectedOption = uiState.theme,
            onChange = { theme ->
                vm.edit {
                    writeEnum(SettingsState::theme, theme)
                }
            },
        )

        SettingsCheckboxFieldFor(SettingsState::showBridgeButton)

//                    ProvideTextStyle(value = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.textSec))
//                    {
//                        Tip("Tap and hold the button to move it.")
//                    }

        SettingsCheckboxFieldFor(SettingsState::showLaunchAppsWhenBridgeButtonCollapsed)

        if (!uiState.isQSTileAdded)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            {
                ActionCard(
                    title = "Quick settings tile",
                    description = "You can add a quick settings tile to unobtrusively toggle the Bridge button. Long-pressing the tile opens this settings screen."
                )
                {
                    val sbm = context.getSystemService(StatusBarManager::class.java)
                    val compName = ComponentName(context, BridgeButtonQSTileService::class.java)

                    Btn(text = "Add tile", suffixIcon = R.drawable.ic_plus, onClick = {
                        sbm.requestAddTileService(
                            compName,
                            "Bridge button",
                            Icon.createWithResource(context, R.drawable.ic_bridge_white),
                            {},
                            {}
                        )
                    })
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