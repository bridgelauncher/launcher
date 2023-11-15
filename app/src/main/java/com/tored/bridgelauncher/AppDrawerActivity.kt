package com.tored.bridgelauncher

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.tored.bridgelauncher.composables.ResIcon
import com.tored.bridgelauncher.ui.shared.SetSystemBarsForBotBarActivity
import com.tored.bridgelauncher.ui.theme.BridgeLauncherTheme
import com.tored.bridgelauncher.ui.theme.botBar
import com.tored.bridgelauncher.ui.theme.textSec
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AppDrawerActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setContent {
            BridgeLauncherTheme {
                AppDrawerScreen()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppDrawerScreen()
{
    SetSystemBarsForBotBarActivity()

    var searchString by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colors.background
    )
    {
        Column()
        {
            val context = LocalContext.current
            val appContext = context.applicationContext as BridgeLauncherApp
            LazyColumn(
                modifier = Modifier
                    .weight(1f),
                reverseLayout = true,
                contentPadding = PaddingValues(0.dp, 8.dp),
            )
            {
                items(appContext.installedAppsHolder.installedApps)
                { app ->
                    var menuOpen by remember { mutableStateOf(false) }
                    var menuOffsetX by remember { mutableFloatStateOf(0f) }

                    Box()
                    {
                        DropdownMenu(
                            expanded = menuOpen,
                            onDismissRequest = { menuOpen = false },
                            offset = DpOffset(LocalDensity.current.run { menuOffsetX.toDp() }, 0.dp)
                        )
                        {
                            val clipman = LocalClipboardManager.current

                            data class action(
                                val iconResId: Int,
                                val label: String,
                                val onClick: () -> Unit,
                            )

                            val items = arrayOf(
                                action(R.drawable.ic_copy, "Copy label")
                                { clipman.setText(AnnotatedString(app.label)) },

                                action(R.drawable.ic_copy, "Copy package name")
                                { clipman.setText(AnnotatedString(app.packageName)) },

                                action(R.drawable.ic_info, "App info")
                                {
                                    try
                                    {
                                        context.startActivity(
                                            Intent(
                                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.parse("package:${app.packageName}")
                                            )
                                        )
                                    }
                                    catch (_: Exception)
                                    {
                                        Toast.makeText(context, "Could not open app info.", Toast.LENGTH_SHORT).show()
                                    }
                                },

                                action(R.drawable.ic_delete, "Uninstall")
                                {
                                    try
                                    {
                                        val packageURI = Uri.parse("package:${app.packageName}")
                                        val uninstallIntent = Intent(Intent.ACTION_DELETE, packageURI)
                                        context.startActivity(uninstallIntent)
//                                        context.packageManager.packageInstaller.uninstall(app.packageName)
                                    }
                                    catch (_: Exception)
                                    {
                                        Toast.makeText(context, "Could not request app uninstall.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                            )

                            for (item in items)
                            {
                                DropdownMenuItem(
                                    content = {
                                        ResIcon(item.iconResId)
                                        Text(item.label, modifier = Modifier.padding(start = 12.dp, end = 8.dp))
                                    },
                                    onClick = {
                                        item.onClick()
                                    }
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 48.dp)
                                .combinedClickable(
                                    onClick =  {
                                        context.startActivity(app.launchIntent)
                                    },
                                    onLongClick = {
                                        menuOpen = true
                                    }
                                )
                                .padding(8.dp, 0.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        )
                        {
                            Box(
                                modifier = Modifier.size(56.dp),
                                contentAlignment = Alignment.Center
                            )
                            {
                                Image(
                                    painter = rememberDrawablePainter(app.icon),
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    contentScale = ContentScale.FillBounds,
                                )
                            }
                            Column()
                            {
                                Text(app.label)
                                Text(app.packageName, color = MaterialTheme.colors.textSec, style = MaterialTheme.typography.body2)
                            }
                        }
                    }
                }
            }
            SearchBotBar(searchString) { searchString = it }
        }
    }
}

@Composable
fun SearchBotBar(searchString: String, onSearchStringChange: (String) -> Unit)
{
    Surface(
        color = MaterialTheme.colors.surface,
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.botBar,
        elevation = 4.dp,
    )
    {
        Row(
            modifier = Modifier
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        )
        {
            val context = LocalContext.current as Activity

            IconButton(onClick = { context.finish() })
            {
                ResIcon(R.drawable.ic_arrow_left)
            }
            TextField(
                value = searchString,
                onValueChange = onSearchStringChange,
                modifier = Modifier
                    .weight(1f),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                singleLine = true,
                placeholder = {
                    Text("Tap to search")
                }
            )
            Spacer(modifier = Modifier.size(48.dp))
//                IconToggleButton(
//                    checked = MaterialTheme.colors.isLight,
//                    onCheckedChange = { /* TODO */ }
//                )
//                {
//                    ResIcon(iconResId = R.drawable.ic_dark_mode)
//                }
        }
    }
}

@Composable
@Preview
fun AppDrawerPreview()
{
    BridgeLauncherTheme {
        AppDrawerScreen()
    }
}