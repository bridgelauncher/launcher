package com.tored.bridgelauncher.ui2.settings.sections.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.ui2.shared.Btn
import com.tored.bridgelauncher.ui2.shared.PreviewWithSurfaceAndPadding

@Composable
fun SettingsScreen2AboutSectionContent(
//    state: SettingsScreen2AboutSectionState,
    modifier: Modifier = Modifier
)
{
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.End,
    )
    {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text("Designed & written by Tored.")
            Text("Bridge Launcher is my attempt at making launcher development approachable by reducing dealing with Android to using a simple API.")
            Text("Contact information available on the project home page. This is to avoid having to update the app just to change some links.")
        }

        val context = LocalContext.current
        Btn(text = "Project home", suffixIcon = R.drawable.ic_open_in_new, onClick = {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/bridgelauncher")))
        })
    }
}

//data class SettingsScreen2AboutSectionState(
//    val isExportDisabled: Boolean,
//)


// PREVIEWS

@Composable
fun SettingsScreen2AboutSectionPreview(
//    isExportDisabled: Boolean = false
)
{
    PreviewWithSurfaceAndPadding {
        SettingsScreen2AboutSectionContent(
//            SettingsScreen2AboutSectionState(
//                isExportDisabled = isExportDisabled
//            )
        )
    }
}

@Composable
@PreviewLightDark
fun SettingsScreen2AboutSectionPreview01()
{
    SettingsScreen2AboutSectionPreview()
}
