package com.tored.bridgelauncher.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.composables.Btn
import com.tored.bridgelauncher.ui.theme.textSec
import com.tored.bridgelauncher.utils.startBridgeSettingsActivity

@Composable
fun HomeScreenNoStoragePermsPrompt(modifier: Modifier = Modifier)
{
    HomeScreenPrompt(
        modifier = modifier,
        title = "Storage permission required",
        message = "Bridge needs access to storage to load project files."
    )
}

@Composable
fun HomeScreenNoProjectPrompt(modifier: Modifier = Modifier)
{
    HomeScreenPrompt(
        modifier = modifier,
        title = "No project loaded",
        message = "You can load a project in Bridge settings."
    )
}

@Composable
fun HomeScreenPrompt(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
)
{
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    )
    {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            color = MaterialTheme.colors.surface,
            shape = MaterialTheme.shapes.large,
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, top = 32.dp, end = 32.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            )
            {
                Text(title)
                Text(
                    message,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.textSec,
                )

                val context = LocalContext.current
                Btn(
                    text = "Open Bridge settings",
                    outlined = true,
                ) {
                    context.startBridgeSettingsActivity()
                }
            }
        }
    }
}
