package com.tored.bridgelauncher.ui2.settings.sections.wallpaper

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.services.settings2.BridgeSettings
import com.tored.bridgelauncher.ui2.shared.Btn
import com.tored.bridgelauncher.ui2.shared.CheckboxField
import com.tored.bridgelauncher.ui2.shared.PreviewWithSurfaceAndPadding
import com.tored.bridgelauncher.ui2.shared.Tip
import com.tored.bridgelauncher.ui2.theme.monoSec
import com.tored.bridgelauncher.ui2.theme.textSec

@Composable
fun SettingsScreen2WallpaperSectionContent(
    state: SettingsScreen2WallpaperSectionState,
    actions: SettingsScreen2WallpaperSectionActions,
    modifier: Modifier = Modifier
)
{
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier,
    )
    {
        Btn(
            text = "Change system wallpaper",
            outlined = true,
            onClick = { actions.changeSystemWallpaper() },
            modifier = Modifier.fillMaxWidth(),
        )

        CheckboxField(
            label = BridgeSettings.drawSystemWallpaperBehindWebView.displayName,
            isChecked = state.drawSystemWallpaperBehindWebView,
            onCheckedChange = { actions.changeDrawSystemWallpaperBehindWebView(it) }
        )

        Tip(
            contentColor = MaterialTheme.colors.textSec,
        )
        {
            Text(
                text = buildAnnotatedString {
                    val monoStyle = MaterialTheme.typography.monoSec.toSpanStyle()
                    append("For the system wallpaper to be visible, both the ")
                    withStyle(monoStyle) { append("html") }
                    append(" and ")
                    withStyle(monoStyle) { append("body") }
                    append(" elements must have a transparent background.")
                    appendLine()
                    appendLine()
                    append("Please keep in mind that effects such as ")
                    withStyle(monoStyle) { append("backdrop-filter") }
                    append(" don't consider the system wallpaper, as it is not part of the webpage.")
                },
                style = MaterialTheme.typography.body2,
            )
        }
    }
}


// PREVIEWS

@Composable
fun SettingsScreenWallpaperSectionContentPreview(
    drawSystemWallpaperBehindWebView: Boolean = false,
)
{
    PreviewWithSurfaceAndPadding {
        SettingsScreen2WallpaperSectionContent(
            state = SettingsScreen2WallpaperSectionState(
                drawSystemWallpaperBehindWebView = drawSystemWallpaperBehindWebView,
            ),
            actions = SettingsScreen2WallpaperSectionActions.empty()
        )
    }
}

@Composable
@PreviewLightDark
fun SettingsScreenWallpaperSectionContentPreview01()
{
    SettingsScreenWallpaperSectionContentPreview()
}

@Composable
@PreviewLightDark
fun SettingsScreenWallpaperSectionContentPreview02()
{
    SettingsScreenWallpaperSectionContentPreview(drawSystemWallpaperBehindWebView = true)
}