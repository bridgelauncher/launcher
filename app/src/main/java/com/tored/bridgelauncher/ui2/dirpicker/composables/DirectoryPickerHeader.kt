package com.tored.bridgelauncher.ui2.dirpicker.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.ui.theme.textSec
import com.tored.bridgelauncher.ui2.DirectoryPicker.composables.DirectoryPickerUpDisabledListItem
import com.tored.bridgelauncher.ui2.DirectoryPicker.composables.DirectoryPickerUpListItem
import com.tored.bridgelauncher.ui2.dirpicker.DirectoryPickerDirectory
import com.tored.bridgelauncher.ui2.dirpicker.DirectoryPickerDummyDirectory
import com.tored.bridgelauncher.ui2.shared.PreviewWithSurfaceAndPadding

@Composable
fun DirectoryPickerHeader(
    currentDir: DirectoryPickerDirectory,
    upDir: DirectoryPickerDirectory?,
    requestUp: () -> Unit,
)
{
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
    )
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        )
        {
            Text(
                "Current directory",
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.textSec
            )
            Text(
                buildAnnotatedString {
                    val canonPath = currentDir.path
                    val match = "(.*/)?(.+)$".toRegex().find(canonPath)!!
                    val path = match.groups[1]?.value ?: ""
                    val curr = match.groups[2]?.value ?: ""

                    append(path)
                    withStyle(SpanStyle(color = MaterialTheme.colors.primary))
                    {
                        append(curr)
                    }
                }
            )
        }

        Divider()

        if (upDir?.canRead == true)
        {
            DirectoryPickerUpListItem(
                modifier = Modifier.fillMaxWidth(),
                upDir = upDir,
                onClick = { requestUp() },
            )
        }
        else
        {
            DirectoryPickerUpDisabledListItem(
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}


// PREVIEW

@Composable
private fun DirectoryPickerHeaderPreview(
    currentDir: DirectoryPickerDirectory,
    upDir: DirectoryPickerDirectory,
)
{
    PreviewWithSurfaceAndPadding {
        DirectoryPickerHeader(currentDir = currentDir, upDir = upDir, requestUp = {})
    }
}

@Composable
@PreviewLightDark
private fun DirectoryPickerHeaderPreview01()
{
    DirectoryPickerHeaderPreview(
        currentDir = DirectoryPickerDummyDirectory("dog", "/up/dog"),
        upDir = DirectoryPickerDummyDirectory("whats", "/whats/up/dog")
    )
}