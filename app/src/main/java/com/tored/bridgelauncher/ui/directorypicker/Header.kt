package com.tored.bridgelauncher.ui.directorypicker

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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.ui.theme.textSec

@Composable
fun DirectoryPickerHeader(
    currentDir: Directory,
    upDir: Directory?,
    onNavigateRequest: (Directory) -> Unit,
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
                    val canonPath = currentDir.canonicalPath
                    val match = "(.*/)?(.+)$".toRegex().find(canonPath)!!
                    val path = match.groups[1]?.value ?: ""
                    val curr = match.groups[2]?.value ?: ""

                    append(path)
                    append(
                        AnnotatedString(
                            curr,
                            spanStyle = SpanStyle(
                                color = MaterialTheme.colors.primary
                            )
                        )
                    )
                }
            )
        }

        Divider()

        if (upDir == null)
        {
            DirectoryPickerUpDisabledListItem(
                modifier = Modifier.fillMaxWidth(),
            )
        }
        else
        {
            DirectoryPickerUpListItem(
                modifier = Modifier.fillMaxWidth(),
                upDir = upDir,
            )
            {
                onNavigateRequest(upDir)
            }
        }
    }
}