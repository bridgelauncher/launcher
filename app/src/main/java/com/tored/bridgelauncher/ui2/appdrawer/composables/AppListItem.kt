package com.tored.bridgelauncher.ui2.appdrawer.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.services.apps.InstalledApp
import com.tored.bridgelauncher.ui2.appdrawer.IAppDrawerApp
import com.tored.bridgelauncher.ui2.appdrawer.TestApps
import com.tored.bridgelauncher.ui2.shared.PreviewWithSurfaceAndPadding
import com.tored.bridgelauncher.ui2.theme.textSec
import com.tored.bridgelauncher.utils.tryLaunchApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

@Composable
fun AppListItem(
    app: IAppDrawerApp,
    getIconFunc: AppIconGetIconFunc,
    onTap: () -> Unit,
    onLongPress: (offset: Offset) -> Unit,
    modifier: Modifier = Modifier,
)
{
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .indication(interactionSource, LocalIndication.current)
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
                        onTap()
                        (app as? InstalledApp)?.let { context.tryLaunchApp(it) }
                    },

                    onLongPress = { offset ->
                        onLongPress(offset)
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
            if (app is InstalledApp)
            {
                AppIcon(
                    iconPack = null,
                    app = app,
                    getIconFunc = getIconFunc,
                    modifier = Modifier.size(40.dp)
                )
            }
            else
            {
                Image(
                    painter = painterResource(if (MaterialTheme.colors.isLight) R.drawable.ic_bridge else R.drawable.ic_bridge_white),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.size(40.dp),
                )
            }
        }
        Column()
        {
            Text(app.label)
            Text(app.packageName, color = MaterialTheme.colors.textSec, style = MaterialTheme.typography.body2)
        }
    }
}


// PREVIEWS

@Composable
fun AppListItemPreview(
    app: IAppDrawerApp = TestApps.App1
)
{
    PreviewWithSurfaceAndPadding {
        AppListItem(
            app = app,
            getIconFunc = emptyGetIconFunc,
            onTap = {},
            onLongPress = {}
        )
    }
}

@Composable
@PreviewLightDark
fun AppListItemPreview_01()
{
    AppListItemPreview()
}