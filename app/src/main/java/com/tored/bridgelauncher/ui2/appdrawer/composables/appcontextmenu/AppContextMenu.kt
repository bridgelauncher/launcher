package com.tored.bridgelauncher.ui2.appdrawer.composables.appcontextmenu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.ui.theme.BridgeLauncherTheme
import com.tored.bridgelauncher.ui2.appdrawer.IAppDrawerApp
import com.tored.bridgelauncher.ui2.appdrawer.TestApps
import com.tored.bridgelauncher.utils.tryOpenAppInfo
import com.tored.bridgelauncher.utils.tryRequestAppUninstall

@Composable
fun AppContextMenu(
    state: AppContextMenuState,
    onDismissRequest: () -> Unit,
)
{
    val context = LocalContext.current
    val clipman = LocalClipboardManager.current

    Popup(
        offset = state.offset,
        alignment = state.alignment,
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
                AppContextMenuItem(R.drawable.ic_copy, "Copy label")
                {
                    clipman.setText(AnnotatedString(state.app.label))
                    onDismissRequest()
                }

                AppContextMenuItem(R.drawable.ic_copy, "Copy package name")
                {
                    clipman.setText(AnnotatedString(state.app.packageName))
                    onDismissRequest()
                }

                AppContextMenuItem(R.drawable.ic_info, "App info")
                {
                    context.tryOpenAppInfo(state.app.packageName)
                    onDismissRequest()
                }

                AppContextMenuItem(R.drawable.ic_delete, "Uninstall")
                {
                    context.tryRequestAppUninstall(state.app.packageName)
                    onDismissRequest()
                }
            }
        }
    }
}


// PREVIEWS

@Composable
fun AppContextMenuPreview(
    app: IAppDrawerApp = TestApps.App1,
)
{
    BridgeLauncherTheme {
        AppContextMenu(
            state = AppContextMenuState(
                app = app,
                offset = IntOffset(0, 0),
                alignment = Alignment.TopStart,
            ),
            onDismissRequest = {}
        )
    }
}

@Composable
@PreviewLightDark
fun AppContextMenuPreview_01()
{
    AppContextMenuPreview()
}