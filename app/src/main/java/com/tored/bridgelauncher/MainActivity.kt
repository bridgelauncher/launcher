package com.tored.bridgelauncher

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager.LayoutParams
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tored.bridgelauncher.composables.ResIcon
import com.tored.bridgelauncher.settings.SettingsState
import com.tored.bridgelauncher.settings.SettingsVM
import com.tored.bridgelauncher.settings.writeBool
import com.tored.bridgelauncher.ui.theme.BridgeLauncherTheme
import com.tored.bridgelauncher.webview.WebView
import com.tored.bridgelauncher.webview.rememberWebViewState
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setContent {
            BridgeLauncherTheme {
                HomeScreen()
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HomeScreen(settingsVM: SettingsVM = viewModel())
{
    val settingsState by settingsVM.settingsUIState.collectAsStateWithLifecycle()
    LaunchedEffect(settingsVM) { settingsVM.request() }

    val webViewState = rememberWebViewState(url = "http://localhost:5000")
    LaunchedEffect(webViewState)
    {
        webViewState.webView?.settings?.javaScriptEnabled = true
    }


    val currentView = LocalView.current
    if (!currentView.isInEditMode)
    {
        val showWallpaper = settingsState.drawSystemWallpaperBehindWebView
        val statusBarAppearance = settingsState.statusBarAppearance
        val navigationBarAppearance = settingsState.navigationBarAppearance

        val currentWindow = (currentView.context as? Activity)?.window
            ?: throw Exception("Attempt to access a window from outside an activity.")

        SideEffect()
        {
            val insetsController = WindowCompat.getInsetsController(currentWindow, currentView)

            WindowCompat.setDecorFitsSystemWindows(currentWindow, false)

            if (showWallpaper)
                currentWindow.addFlags(LayoutParams.FLAG_SHOW_WALLPAPER)
            else
                currentWindow.clearFlags(LayoutParams.FLAG_SHOW_WALLPAPER)

            insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                currentWindow.isNavigationBarContrastEnforced = false
            }

            if (statusBarAppearance == SystemBarAppearanceOptions.Hide)
            {
                insetsController.hide(WindowInsetsCompat.Type.statusBars())
            }
            else
            {
                insetsController.show(WindowInsetsCompat.Type.statusBars())
                insetsController.isAppearanceLightStatusBars = statusBarAppearance == SystemBarAppearanceOptions.DarkIcons
            }

            if (navigationBarAppearance == SystemBarAppearanceOptions.Hide)
            {
                insetsController.hide(WindowInsetsCompat.Type.navigationBars())
            }
            else
            {
                insetsController.show(WindowInsetsCompat.Type.navigationBars())
                insetsController.isAppearanceLightNavigationBars = navigationBarAppearance == SystemBarAppearanceOptions.DarkIcons
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent)
    {
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .fillMaxSize(),
        )
        {

            WebView(
                state = webViewState,
                modifier = Modifier.fillMaxSize(),
            )

            if (settingsState.showBridgeButton)
            {
                Box(
                    modifier = Modifier
                        .padding(16.dp),
                )
                {
                    var isExpanded by rememberSaveable { mutableStateOf(false) }

                    BridgeButtonStateless(
                        isExpanded,
                        onIsExpandedChange = { isExpanded = it },
                        onWebViewRefreshRequest = { webViewState.webView?.reload() },
                    )
                }
            }
        }
    }
}

@Composable
fun BridgeButtonStateless(
    isExpanded: Boolean,
    onIsExpandedChange: (newState: Boolean) -> Unit,
    onWebViewRefreshRequest: () -> Unit,
    settingsVM: SettingsVM = viewModel(),
)
{
    val settingsState by settingsVM.settingsUIState.collectAsStateWithLifecycle()

    Row(
        modifier = Modifier
            .wrapContentSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    )
    {
        // label column
        if (isExpanded)
        {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
            )
            {
                TouchTargetLabel(text = "Refresh WebView")
                TouchTargetLabel(text = "Developer console")

                Divider(
                    modifier = Modifier.width(56.dp),
                    color = Color.Transparent,
                )

                TouchTargetLabel(text = "Switch away from Bridge")
                TouchTargetLabel(text = "Bridge settings")
                TouchTargetLabel(text = "Hide Bridge button")
                TouchTargetLabel(text = "Built-in app drawer")

                Divider(
                    modifier = Modifier.width(56.dp),
                    color = Color.Transparent,
                )

                TouchTargetLabel(text = "Collapse this menu")
            }
        }

        Surface(
            modifier = Modifier
                .wrapContentSize(),
            color = MaterialTheme.colors.surface,
            shape = MaterialTheme.shapes.large,
            elevation = 4.dp,
        )
        {
            val context = LocalContext.current

            // button column
            Column(
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.End,
            )
            {
                if (isExpanded)
                {
                    TouchTarget(iconResId = R.drawable.ic_refresh) {
                        onWebViewRefreshRequest()
                    }
                    TouchTarget(iconResId = R.drawable.ic_dev_console) { }

                    Divider()

                    TouchTarget(iconResId = R.drawable.ic_switch_launchers)
                    {
                        context.startActivity(
                            Intent(Settings.ACTION_HOME_SETTINGS).apply()
                            {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                        )
                    }

                    TouchTarget(iconResId = R.drawable.ic_settings)
                    {
                        context.startActivity(Intent(context, SettingsActivity::class.java))
                    }

                    TouchTarget(iconResId = R.drawable.ic_hide)
                    {
                        settingsVM.edit {
                            writeBool(SettingsState::showBridgeButton, false)
                        }
                    }
                }

                if (isExpanded || settingsState.showLaunchAppsWhenBridgeButtonCollapsed)
                {
                    TouchTarget(iconResId = R.drawable.ic_apps)
                    {
                        context.startActivity(Intent(context, AppDrawerActivity::class.java))
                    }

                    Divider()
                }

                TouchTarget(iconResId = R.drawable.ic_bridge)
                {
                    onIsExpandedChange(!isExpanded)
                }
            }

        }
    }
}


@Composable
fun TouchTarget(iconResId: Int, onClick: () -> Unit)
{
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .size(56.dp),
        contentAlignment = Alignment.Center,
    )
    {
        ResIcon(iconResId = iconResId)
    }
}

@Composable
fun TouchTargetLabel(text: String)
{
    Box(
        modifier = Modifier
            .height(56.dp)
            .wrapContentWidth(),
        contentAlignment = Alignment.CenterEnd,
    )
    {
        Surface(
            shape = MaterialTheme.shapes.small,
            elevation = 4.dp
        )
        {
            Text(
                text = text,
                modifier = Modifier
                    .padding(16.dp, 8.dp),
            )
        }
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0x000000)
fun DefaultPreview()
{
    BridgeLauncherTheme {

    }
}