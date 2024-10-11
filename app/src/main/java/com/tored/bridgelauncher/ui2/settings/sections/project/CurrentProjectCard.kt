package com.tored.bridgelauncher.ui2.settings.sections.project

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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.composables.Btn
import com.tored.bridgelauncher.ui.settings.CurrentProjectCardAlert
import com.tored.bridgelauncher.ui.theme.BridgeLauncherThemeStateless
import com.tored.bridgelauncher.ui.theme.borders
import com.tored.bridgelauncher.ui.theme.textSec

@Composable
fun CurrentProjectCard(
    projectInfo: SettingsScreenProjectSectionStateProjectInfo?,
    hasStoragePerms: Boolean,
    onChangeClick: () -> Unit,
    onGrantPermissionRequest: () -> Unit,
    modifier: Modifier = Modifier
)
{
    Surface(
        modifier = modifier
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
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                )
                {
                    Text(
                        text = "Current project",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.textSec
                    )
                    if (projectInfo == null)
                    {
                        Text(
                            text = "Not selected yet.",
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.textSec,
                        )
                    }
                    else
                    {
                        Text(
                            text = projectInfo.name,
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.primary,
                        )
                    }
                }

                if (projectInfo == null)
                {
                    Btn(
                        text = "Select",
                        contentColor = MaterialTheme.colors.primary,
                        onClick = onChangeClick
                    )
                }
                else
                {
                    Btn(
                        text = "Change",
                        contentColor = MaterialTheme.colors.onSurface,
                        onClick = onChangeClick
                    )
                }
            }

            if (projectInfo != null && !hasStoragePerms)
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


// PREVIEWS

@Composable
fun CurrentProjectCardPreview(
    projectInfo: SettingsScreenProjectSectionStateProjectInfo?,
    hasStoragePerms: Boolean,
)
{
    BridgeLauncherThemeStateless {
        CurrentProjectCard(
            projectInfo = projectInfo,
            hasStoragePerms = hasStoragePerms,
            onChangeClick = {},
            onGrantPermissionRequest = {},
        )
    }
}

@Composable
@PreviewLightDark
private fun SettingsScreenProjectSectionPreview_NoProjectNoPerms()
{
    CurrentProjectCardPreview(
        projectInfo = null,
        hasStoragePerms = false,
    )
}

@Composable
@PreviewLightDark
private fun SettingsScreenProjectSectionPreview_NoProjectWithPerms()
{
    CurrentProjectCardPreview(
        projectInfo = null,
        hasStoragePerms = true,
    )
}

@Composable
@PreviewLightDark
private fun SettingsScreenProjectSectionPreview_ProjectWithNoPerms()
{
    CurrentProjectCardPreview(
        projectInfo = SettingsScreenProjectSectionStateProjectInfo("SampleProjectName"),
        hasStoragePerms = false,
    )
}

@Composable
@PreviewLightDark
private fun SettingsScreenProjectSectionPreview_ProjectWithPerms()
{
    CurrentProjectCardPreview(
        projectInfo = SettingsScreenProjectSectionStateProjectInfo("SampleProjectName"),
        hasStoragePerms = true,
    )
}