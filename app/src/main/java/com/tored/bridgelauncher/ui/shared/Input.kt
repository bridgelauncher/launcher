package com.tored.bridgelauncher.ui.shared

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.ui.theme.borders
import com.tored.bridgelauncher.ui.theme.checkedItemBg

@Composable
fun CheckboxField(label: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit)
{
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(8.dp),
        color = if (isChecked)
            MaterialTheme.colors.checkedItemBg
        else
            Color.Transparent
    )
    {
        Row(
            modifier = Modifier
                .clickable { onCheckedChange(!isChecked) }
                .defaultMinSize(minHeight = 48.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(12.dp, 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        )
        {
            Checkbox(
                checked = isChecked,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colors.onSurface,
                    uncheckedColor = MaterialTheme.colors.onSurface,
                )
            )
            Text(label)
        }
    }
}

@Composable
fun <TOption> OptionsRow(label: String, options: Map<TOption, String>, selectedOption: TOption, onChange: (TOption) -> Unit)
{
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    )
    {
        Text(label, modifier = Modifier.padding(4.dp, 0.dp))

        Row(
            modifier = Modifier
                .border(MaterialTheme.borders.soft, RoundedCornerShape(9.dp))
                .padding(1.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
        )
        {

            for (entry in options)
            {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 48.dp),
                    color = if (selectedOption == entry.key)
                        MaterialTheme.colors.checkedItemBg
                    else
                        Color.Transparent,
                    shape = RoundedCornerShape(8.dp),
                )
                {
                    Box(
                        modifier = Modifier
                            .clickable { onChange(entry.key) },
                        contentAlignment = Alignment.Center,
                    )
                    {
                        Text(entry.value)
                    }
                }
            }
        }
    }
}