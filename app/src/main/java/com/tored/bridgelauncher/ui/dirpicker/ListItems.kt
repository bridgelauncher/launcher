package com.tored.bridgelauncher.ui.dirpicker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.composables.ResIcon
import java.io.File

@Composable
fun DirPickerUpListItem(
    modifier: Modifier = Modifier,
    upDir: Directory,
    onClick: () -> Unit,
)
{
    DirPickerListItem(
        modifier = modifier,
        priIconResId = R.drawable.ic_folder_up,
        priText = "Go up",
        secText = "to ${upDir.name}",
        onClick = onClick,
    )
}

@Composable
fun DirPickerUpDisabledListItem(
    modifier: Modifier = Modifier,
)
{
    DirPickerListItem(
        modifier = modifier,
        priIconResId = R.drawable.ic_folder_up,
        priText = "Can't go up"
    )
}

@Composable
fun DirPickerSubdirListItem(
    dir: Directory,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
)
{
    DirPickerListItem(
        modifier = modifier,
        priIconResId = R.drawable.ic_folder_open,
        priText = dir.name,
        secText = "/",
        showSuffixArrow = true,
        onClick = onClick,
    )
}

@Composable
fun DirPickerSubfileListItem(file: File)
{
    DirPickerListItem(
        priIconResId = R.drawable.ic_file,
        priText = file.name,
    )
}

@Composable
fun DirPickerStartupSubfileListItem(file: File)
{
    DirPickerListItem(
        priIconResId = R.drawable.ic_file_star_filled,
        priText = file.name,
        secText = "Startup file",
        color = MaterialTheme.colors.primary,
        forceFullAlpha = true,
    )
}

@Composable
fun DirPickerListItem(
    priIconResId: Int,
    priText: String,
    modifier: Modifier = Modifier,
    secText: String = "",
    showSuffixArrow: Boolean = false,
    color: Color = MaterialTheme.colors.onSurface,
    forceFullAlpha: Boolean = false,
    onClick: (() -> Unit)? = null,
)
{
    Row(
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .run {
                if (onClick == null)
                    this
                else
                    clickable { onClick() }
            }
            .padding(16.dp, 8.dp)
            .alpha(if (onClick == null && !forceFullAlpha) 0.4f else 1f),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    )
    {
        CompositionLocalProvider(LocalContentColor provides color)
        {
            ResIcon(priIconResId)

            Row(
                modifier = Modifier
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            )
            {

                Text(priText, color = LocalContentColor.current)

                if (secText.isNotEmpty())
                {
                    Text(
                        secText,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.alpha(0.6f),
                    )
                }
            }

            if (showSuffixArrow)
            {
                ResIcon(
                    R.drawable.ic_arrow_right_thin,
                    modifier = Modifier.alpha(0.4f),
                )
            }
        }
    }
}