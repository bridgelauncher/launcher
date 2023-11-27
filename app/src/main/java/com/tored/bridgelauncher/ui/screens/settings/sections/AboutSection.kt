package com.tored.bridgelauncher.ui.screens.settings.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            Btn(text = "GitHub repository", suffixIcon = R.drawable.ic_open_in_new, onClick = { /* TODO */ })
            Btn(text = "Discord server", suffixIcon = R.drawable.ic_open_in_new, onClick = { /* TODO */ })
            Btn(text = "Send me an email", suffixIcon = R.drawable.ic_arrow_right, onClick = { /* TODO */ })
            Btn(text = "Copy my email address", suffixIcon = R.drawable.ic_copy, onClick = { /* TODO */ })
        }
    }
}