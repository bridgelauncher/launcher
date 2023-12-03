package com.tored.bridgelauncher.ui.dirpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.ui.theme.borderLight
import com.tored.bridgelauncher.ui.theme.textSec

@Composable
fun DirPickerProgressBar(
    completedCount: Int,
    startedCount: Int,
    modifier: Modifier
)
{
    Column(
        modifier = modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    )
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        )
        {
            Text("Exporting...")
            Text(
                buildAnnotatedString {
                    append(AnnotatedString(completedCount.toString(), spanStyle = SpanStyle(color = MaterialTheme.colors.primary)))
                    append(AnnotatedString("/", spanStyle = SpanStyle(color = MaterialTheme.colors.textSec)))
                    append(AnnotatedString(startedCount.toString(), spanStyle = SpanStyle(color = MaterialTheme.colors.onSurface)))
                },
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
        )
        {
            val f = completedCount.toFloat() / startedCount

            if (f > 0)
            {
                Spacer(
                    modifier = modifier
                        .weight(f)
                        .fillMaxHeight()
                        .background(MaterialTheme.colors.primary),
                )
            }

            if (1 - f > 0)
            {
                Spacer(
                    modifier = modifier
                        .weight(1 - f)
                        .fillMaxHeight()
                        .background(MaterialTheme.colors.borderLight),
                )
            }
        }
    }
}

@Composable
fun DirPickerProgressBar(
    exportState: DirPickerExportState.Exporting,
    modifier: Modifier = Modifier,
)
{
    DirPickerProgressBar(
        completedCount = exportState.completedCount,
        startedCount = exportState.startedCount,
        modifier = modifier,
    )
}