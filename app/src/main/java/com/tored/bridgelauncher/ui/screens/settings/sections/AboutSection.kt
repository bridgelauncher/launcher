package com.tored.bridgelauncher.ui.screens.settings.sections

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.composables.Btn
import com.tored.bridgelauncher.ui.screens.settings.SettingsSection

@Composable
fun SettingsAboutSection()
{
    SettingsSection(label = "About & Contact", iconResId = R.drawable.ic_about)
    {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.End,
        )
        {
            Text("Bridge Launcher is an attempt at making launcher development approachable by reducing dealing with Android to using a simple API.\n"
                    + "Designed & written by Tored.")

            val context=  LocalContext.current
            Btn(text = "GitHub repository", suffixIcon = R.drawable.ic_open_in_new, onClick = {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/theothertored/bridgelauncher")))
            })
        }
    }
}