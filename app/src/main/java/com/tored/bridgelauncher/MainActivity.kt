package com.tored.bridgelauncher

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager.LayoutParams
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.tored.bridgelauncher.ui.theme.BridgeLauncherTheme
import com.tored.bridgelauncher.vms.SettingsVM
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

@Composable
fun HomeScreen(settingsVM: SettingsVM = viewModel())
{
    val settingsUIState by settingsVM.settingsUIState.collectAsStateWithLifecycle()
    LaunchedEffect(settingsVM) { settingsVM.request() }

    val currentView = LocalView.current
    if (!currentView.isInEditMode)
    {
        val showWallpaper = settingsUIState.drawSystemWallpaperBehindWebView
        val statusBarAppearance = settingsUIState.statusBarAppearance
        val navigationBarAppearance = settingsUIState.navigationBarAppearance

        val currentWindow = (currentView.context as? Activity)?.window
            ?: throw Exception("Attempt to access a window from outside an activity.")

        SideEffect()
        {
            val insetsController = WindowCompat.getInsetsController(currentWindow, currentView)

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
                .fillMaxSize()
                .padding(16.dp),
        )
        {
            BridgeButtonStateful(false)
        }
    }
}

@Composable
fun BridgeButtonStateless(isExpanded: Boolean, onIsExpandedChange: (newState: Boolean) -> Unit)
{
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
            shape = RoundedCornerShape(8.dp),
            elevation = 4.dp,
        )
        {
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
                    val context = LocalContext.current

                    TouchTarget(iconResId = R.drawable.ic_refresh) { }
                    TouchTarget(iconResId = R.drawable.ic_dev_console) { }

                    Divider()

                    TouchTarget(iconResId = R.drawable.ic_switch_launchers) { }
                    TouchTarget(iconResId = R.drawable.ic_settings)
                    {
                        context.startActivity(Intent(context, SettingsActivity::class.java))
                    }
                    TouchTarget(iconResId = R.drawable.ic_hide) { }
                    TouchTarget(iconResId = R.drawable.ic_apps) { }

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
fun BridgeButtonStateful(startExpanded: Boolean)
{
    var isExpanded by rememberSaveable { mutableStateOf(startExpanded) }
    BridgeButtonStateless(
        isExpanded = isExpanded,
        onIsExpandedChange = { isExpanded = it }
    )
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
            shape = RoundedCornerShape(4.dp),
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
        BridgeButtonStateful(true)
    }
}