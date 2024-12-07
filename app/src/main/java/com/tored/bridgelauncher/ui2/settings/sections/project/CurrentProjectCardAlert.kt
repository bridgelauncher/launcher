package com.tored.bridgelauncher.ui2.settings.sections.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.ui2.shared.ResIcon

@Composable
fun CurrentProjectCardAlert(modifier: Modifier = Modifier)
{
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    )
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        )
        {
            ResIcon(iconResId = R.drawable.ic_warning, color = MaterialTheme.colors.error)
            Text("Project cannot be loaded", color = MaterialTheme.colors.error)
            Text(
                text = "Bridge does not have permission to access files. " +
                        "Click this warning to grant the permission.",
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
            )
        }
    }
}