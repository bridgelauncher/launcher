package com.tored.bridgelauncher

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.tored.bridgelauncher.composables.ResIcon
import com.tored.bridgelauncher.services.apps.InstalledApp
import com.tored.bridgelauncher.services.apps.InstalledAppsHolder
import com.tored.bridgelauncher.ui.shared.SetSystemBarsForBotBarActivity
import com.tored.bridgelauncher.ui.shared.TextFieldPlaceholderDecorationBox
import com.tored.bridgelauncher.ui.theme.BridgeLauncherTheme
import com.tored.bridgelauncher.ui.theme.botBar
import com.tored.bridgelauncher.ui.theme.textPlaceholder
import com.tored.bridgelauncher.ui.theme.textSec
import com.tored.bridgelauncher.utils.launchApp
import com.tored.bridgelauncher.utils.openAppInfo
import com.tored.bridgelauncher.utils.requestAppUninstall
import com.tored.bridgelauncher.utils.showErrorToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class AppDrawerActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        val bridge = applicationContext as BridgeLauncherApplication
        val installedAppsHolder = bridge.serviceProvider.installedAppsHolder

        setContent {
            BridgeLauncherTheme {
                AppDrawerScreen(installedAppsHolder)
            }
        }
    }
}

@Composable
fun AppDrawerScreen(
    installedAppsHolder: InstalledAppsHolder,
)
{
    SetSystemBarsForBotBarActivity()

    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current

    var searchString by remember { mutableStateOf("") }
    val searchStringTrimmed = searchString.trim().lowercase()
    val searchStringSimplified = InstalledApp.simplifyLabel(searchString)

    val filteredApps = installedAppsHolder.installedApps.values
        .filter {
            it.labelSimplified.contains(searchStringSimplified)
                    || it.packageName.lowercase().contains(searchStringTrimmed)
        }
        .sortedBy { it.label }

    var dropdownOpenFor by remember { mutableStateOf<InstalledApp?>(null) }
    var dropdownItemInLazyColOffset by remember { mutableStateOf(Offset(0f, 0f)) }
    var dropdownTouchOffset by remember { mutableStateOf(Offset(0f, 0f)) }
    var dropdownParentSize by remember { mutableStateOf(IntSize(0, 0)) }

    var dropdownFinalOffset = (dropdownItemInLazyColOffset + dropdownTouchOffset).toIntOffset()
    val dropToLeft = dropdownFinalOffset.x > dropdownParentSize.width / 2
    val dropUp = dropdownFinalOffset.y > dropdownParentSize.height / 2

    val dropdownAlignment = if (dropToLeft)
    {
        if (dropUp)
            Alignment.BottomEnd
        else
            Alignment.TopEnd
    }
    else
    {
        if (dropUp)
            Alignment.BottomStart
        else
            Alignment.TopStart
    }

    if (dropToLeft)
        dropdownFinalOffset = dropdownFinalOffset.copy(x = dropdownFinalOffset.x - dropdownParentSize.width)

    if (dropUp)
        dropdownFinalOffset = dropdownFinalOffset.copy(y = dropdownFinalOffset.y - dropdownParentSize.height)

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colors.background
    )
    {
        Column()
        {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .onGloballyPositioned {
                        dropdownParentSize = it.size
                    }
            )
            {
                AppContextMenu(
                    showForApp = dropdownOpenFor,
                    offset = dropdownFinalOffset,
                    alignment = dropdownAlignment,
                    onDismissRequest = {
                        dropdownOpenFor = null
                    }
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    reverseLayout = true,
                    contentPadding = PaddingValues(0.dp, 8.dp),
                )
                {

                    if (filteredApps.any())
                    {
                        items(filteredApps)
                        { app ->

                            val interactionSource = remember { MutableInteractionSource() }
                            var positionInParent by remember { mutableStateOf(Offset(0f, 0f)) }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = 48.dp)
                                    .indication(interactionSource, LocalIndication.current)
                                    .onGloballyPositioned {
                                        positionInParent = it.positionInParent()
                                    }
                                    .pointerInput(app)
                                    {
                                        detectTapGestures(
                                            onPress = { offset ->
                                                val press = PressInteraction.Press(offset)

                                                val pressAfterDelayJob = CoroutineScope(coroutineContext).launch {
                                                    delay(100)
                                                    interactionSource.emit(press)
                                                }

                                                val gotCancelled = !tryAwaitRelease()
                                                val wasNotEmitted = !pressAfterDelayJob.isCompleted

                                                if (wasNotEmitted && gotCancelled)
                                                {
                                                    pressAfterDelayJob.cancel()
                                                }
                                                else
                                                {
                                                    if (wasNotEmitted)
                                                        interactionSource.emit(press)

                                                    interactionSource.emit(
                                                        if (gotCancelled)
                                                            PressInteraction.Release(press)
                                                        else
                                                            PressInteraction.Cancel(press)
                                                    )
                                                }
                                            },

                                            onTap = {
                                                try
                                                {
                                                    context.launchApp(app)
                                                }
                                                catch (ex: Exception)
                                                {
                                                    context.showErrorToast(ex)
                                                }
                                            },

                                            onLongPress = { offset ->
                                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                                dropdownItemInLazyColOffset = positionInParent
                                                dropdownTouchOffset = offset
                                                dropdownOpenFor = app
                                            }
                                        )
                                    }
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
                                        painter = rememberDrawablePainter(app.defaultIcon),
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
                    else
                    {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            )
                            {
                                ResIcon(R.drawable.ic_search, color = MaterialTheme.colors.textSec)
                                Text("No apps matched the filter text.", color = MaterialTheme.colors.textSec)
                            }
                        }
                    }
                }
            }

            SearchBotBar(
                searchString,
                onSearchStringChange = { searchString = it },
                onGoPressed = {
                    if (filteredApps.any())
                    {
                        try
                        {
                            context.launchApp(filteredApps.first())
                        }
                        catch (ex: Exception)
                        {
                            context.showErrorToast(ex)
                        }
                    }
                },
            )
        }
    }
}

private fun Offset.toIntOffset(): IntOffset
{
    return IntOffset(this.x.toInt(), this.y.toInt())
}

@Composable
fun AppContextMenu(
    showForApp: InstalledApp?,
    offset: IntOffset,
    alignment: Alignment,
    onDismissRequest: () -> Unit
)
{
    val context = LocalContext.current
    val clipman = LocalClipboardManager.current

    data class Action(
        val iconResId: Int,
        val label: String,
        val onClick: InstalledApp.() -> Unit,
    )

    val items = remember {
        arrayOf(
            Action(R.drawable.ic_copy, "Copy label")
            {
                clipman.setText(AnnotatedString(label))
            },

            Action(R.drawable.ic_copy, "Copy package name")
            {
                clipman.setText(AnnotatedString(packageName))
            },

            Action(R.drawable.ic_info, "App info")
            {
                try
                {
                    context.openAppInfo(packageName)
                }
                catch (ex: Exception)
                {
                    context.showErrorToast(ex)
                }
            },

            Action(R.drawable.ic_delete, "Uninstall")
            {
                context.requestAppUninstall(packageName)
            },
        )
    }

    @Composable
    fun DropdownItems(app: InstalledApp)
    {
        for (item in items)
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable()
                    {
                        item.onClick(app)
                        onDismissRequest()
                    }
                    .padding(start = 32.dp, end = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
            )
            {
                Text(
                    item.label,
                    modifier = Modifier.width(IntrinsicSize.Max),
                )
                Box(
                    modifier = Modifier
                        .size(48.dp),
                    contentAlignment = Alignment.Center,
                )
                {
                    ResIcon(item.iconResId)
                }
            }
        }
    }

    if (showForApp != null)
    {
        Popup(
            offset = offset,
            alignment = alignment,
            onDismissRequest = onDismissRequest,
        )
        {

            Surface(
                modifier = Modifier,
                color = MaterialTheme.colors.surface,
                shape = MaterialTheme.shapes.large,
                elevation = 8.dp,
            )
            {
                Column(
                    modifier = Modifier
                        .width(IntrinsicSize.Min)
                        .height(IntrinsicSize.Min)
                        .padding(0.dp, 16.dp)
                )
                {

                    DropdownItems(showForApp)
                }
            }
        }
    }
}

@Composable
fun SearchBotBar(searchString: String, onSearchStringChange: (String) -> Unit, onGoPressed: () -> Unit)
{
    val focusRequester = FocusRequester()
    var searchbarHasFocus by remember { mutableStateOf(false) }

    LaunchedEffect(Unit)
    {
        coroutineContext.job.invokeOnCompletion {
            focusRequester.requestFocus()
        }
    }

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

            BasicTextField(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .onFocusChanged { searchbarHasFocus = it.hasFocus },
                value = searchString,
                onValueChange = onSearchStringChange,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Go,
                    autoCorrect = false,
                    keyboardType = KeyboardType.Uri,
                    capitalization = KeyboardCapitalization.None,
                ),
                textStyle = TextStyle(
                    platformStyle = PlatformTextStyle(includeFontPadding = true),
                    color = MaterialTheme.colors.onSurface,
                ),
                keyboardActions = KeyboardActions(
                    onGo = { onGoPressed() }
                ),
                cursorBrush = SolidColor(MaterialTheme.colors.primary),
                singleLine = true,
                decorationBox = { innerTextField ->
                    TextFieldPlaceholderDecorationBox(
                        text = searchString,
                        placeholderText = "Type to search...",
                        innerTextField = innerTextField
                    )
                }
            )

            val clearIsEnabled = searchString.isNotEmpty()
            IconButton(
                enabled = clearIsEnabled,
                onClick = { onSearchStringChange("") })
            {
                ResIcon(
                    R.drawable.ic_close,
                    if (clearIsEnabled)
                        MaterialTheme.colors.onSurface
                    else
                        MaterialTheme.colors.textPlaceholder
                )
            }
        }
    }
}

// TODO: make the app list provider mockable and give this preview with a sample list of apps
//@Composable
//@Preview
//fun AppDrawerPreview()
//{
//    BridgeLauncherTheme {
//        AppDrawerScreen(null)
//    }
//}