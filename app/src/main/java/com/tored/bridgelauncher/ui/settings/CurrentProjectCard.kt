package com.tored.bridgelauncher.ui.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.composables.Btn
import com.tored.bridgelauncher.composables.ResIcon
import com.tored.bridgelauncher.ui.dirpicker.Directory
import com.tored.bridgelauncher.ui.theme.borders
import com.tored.bridgelauncher.ui.theme.textSec

@Composable
fun CurrentProjectCard(
    currentProjDir: Directory?,
    onChangeClick: () -> Unit,
    hasStoragePerms: Boolean,
    onGrantPermissionRequest: () -> Unit,
)
{
    Surface(
        modifier = Modifier
            .border(border = MaterialTheme.borders.soft, shape = MaterialTheme.shapes.large)
            .padding(MaterialTheme.borders.soft.width)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
    )
    {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        )
        {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            )
            {
                Column(
                    modifier = Modifier.weight(1f)
                )
                {
                    Text(
                        text = "Current project",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.textSec
                    )
                    Text(
                        text = currentProjDir?.name ?: "-",
                        style = MaterialTheme.typography.body1,
                    )
                }

                Btn(
                    text = "Change",
                    onClick = onChangeClick
                )
            }

            if (currentProjDir != null && !hasStoragePerms)
            {
                Divider()
                CurrentProjectCardAlert(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onGrantPermissionRequest() }
                        .padding(16.dp)
                )
            }
        }
    }
}

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