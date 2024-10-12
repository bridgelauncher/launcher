package com.tored.bridgelauncher.ui2.settings.sections.wallpaper

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.composables.Btn
import com.tored.bridgelauncher.services.settings.SettingsState
import com.tored.bridgelauncher.ui.shared.CheckboxField
import com.tored.bridgelauncher.ui.shared.Tip
import com.tored.bridgelauncher.ui.theme.textSec
import com.tored.bridgelauncher.ui2.shared.PreviewWithSurfaceAndPadding
import com.tored.bridgelauncher.utils.displayNameFor

@Composable
fun SettingsScreen2WallpaperSectionContent(
    state: SettingsScreen2WallpaperSectionState,
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
            onClick = { TODO() },
            modifier = Modifier.fillMaxWidth(),
        )

        val prop = SettingsState::drawSystemWallpaperBehindWebView
        CheckboxField(
            label = displayNameFor(prop),
            isChecked = state.drawSystemWallpaperBehindWebView,
            onCheckedChange = { TODO() }
        )

        Tip(
            contentColor = MaterialTheme.colors.textSec,
        )
        {
            Text(
                text = buildAnnotatedString {
                    val monoStyle = SpanStyle(fontFamily = FontFamily.Monospace, fontSize = MaterialTheme.typography.body2.fontSize)
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
            )
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