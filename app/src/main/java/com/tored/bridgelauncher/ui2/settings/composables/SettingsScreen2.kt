package com.tored.bridgelauncher.ui2.settings.composables

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.services.settings.SystemBarAppearanceOptions
import com.tored.bridgelauncher.services.settings.ThemeOptions
import com.tored.bridgelauncher.ui.theme.BridgeLauncherThemeStateless
import com.tored.bridgelauncher.ui2.settings.SettingsScreenVM
import com.tored.bridgelauncher.ui2.settings.sections.about.SettingsScreen2AboutSectionContent
import com.tored.bridgelauncher.ui2.settings.sections.bridge.SettingsScreen2BridgeSectionContent
import com.tored.bridgelauncher.ui2.settings.sections.bridge.SettingsScreen2BridgeSectionState
import com.tored.bridgelauncher.ui2.settings.sections.development.SettingsScreen2DevelopmentSectionContent
import com.tored.bridgelauncher.ui2.settings.sections.development.SettingsScreen2DevelopmentSectionState
import com.tored.bridgelauncher.ui2.settings.sections.project.ScreenLockingMethodOptions
import com.tored.bridgelauncher.ui2.settings.sections.project.SettingsScreen2ProjectSectionContent
import com.tored.bridgelauncher.ui2.settings.sections.project.SettingsScreen2ProjectSectionState
import com.tored.bridgelauncher.ui2.settings.sections.project.SettingsScreenProjectSectionStateProjectInfo
import com.tored.bridgelauncher.ui2.settings.sections.systembars.SettingsScreen2OverlaysSectionContent
import com.tored.bridgelauncher.ui2.settings.sections.systembars.SettingsScreen2OverlaysSectionState
import com.tored.bridgelauncher.ui2.settings.sections.wallpaper.SettingsScreen2WallpaperSectionContent
import com.tored.bridgelauncher.ui2.settings.sections.wallpaper.SettingsScreen2WallpaperSectionState
import com.tored.bridgelauncher.ui2.shared.BotBarScreen

@Composable
fun SettingsScreen2(vm: SettingsScreenVM = viewModel())
{
}

@Composable
fun SettingsScreen2(
    projectSectionState: SettingsScreen2ProjectSectionState,
    wallpaperSectionState: SettingsScreen2WallpaperSectionState,
    overlaysSectionState: SettingsScreen2OverlaysSectionState,
    bridgeSectionState: SettingsScreen2BridgeSectionState,
    developmentSectionState: SettingsScreen2DevelopmentSectionState,
)
{
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize(),
    ) {
        BotBarScreen(
            onLeftActionClick = { TODO() },
            titleAreaContent = {
                Text(text = "Settings")
            }
        )
        {
            Column(
                modifier = Modifier.padding(0.dp, 8.dp)
            )
            {
                SettingsScreen2Section(label = "Project", iconResId = R.drawable.ic_folder_open) {
                    SettingsScreen2ProjectSectionContent(
                        state = projectSectionState
                    )
                }

                Divider()

                SettingsScreen2Section(label = "System wallpaper", iconResId = R.drawable.ic_image) {
                    SettingsScreen2WallpaperSectionContent(
                        state = wallpaperSectionState
                    )
                }

                Divider()

                SettingsScreen2Section(label = "Overlays", iconResId = R.drawable.ic_overlays) {
                    SettingsScreen2OverlaysSectionContent(
                        state = overlaysSectionState
                    )
                }

                Divider()

                SettingsScreen2Section(label = "Bridge", iconResId = R.drawable.ic_bridge) {
                    SettingsScreen2BridgeSectionContent(
                        state = bridgeSectionState
                    )
                }

                Divider()

                SettingsScreen2Section(label = "Development", iconResId = R.drawable.ic_tools) {
                    SettingsScreen2DevelopmentSectionContent(
                        state = developmentSectionState
                    )
                }

                Divider()

                SettingsScreen2Section(label = "About Bridge Launcher", iconResId = R.drawable.ic_about) {
                    SettingsScreen2AboutSectionContent()
                }
            }
        }
    }
}


// PREVIEWS

const val settingsScreen2PreviewHeight = 2000

@Composable
@Preview(
    name = "Light",
    heightDp = settingsScreen2PreviewHeight,
)
@Preview(
    name = "Dark",
    uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL,
    heightDp = settingsScreen2PreviewHeight,
)
fun SettingsScreen2Preview01()
{
    BridgeLauncherThemeStateless {
        SettingsScreen2(
            projectSectionState = SettingsScreen2ProjectSectionState(
                projectInfo = SettingsScreenProjectSectionStateProjectInfo("LOL"),
                hasStoragePerms = true,
                allowProjectsToTurnScreenOff = true,
                screenLockingMethod = ScreenLockingMethodOptions.DeviceAdmin,
            ),
            wallpaperSectionState = SettingsScreen2WallpaperSectionState(
                drawSystemWallpaperBehindWebView = true,
            ),
            overlaysSectionState = SettingsScreen2OverlaysSectionState(
                statusBarAppearance = SystemBarAppearanceOptions.Hide,
                navigationBarAppearance = SystemBarAppearanceOptions.LightIcons,
                drawWebViewOverscrollEffects = true,
            ),
            bridgeSectionState = SettingsScreen2BridgeSectionState(
                theme = ThemeOptions.System,
                isQSTilePromptSupported = true,
                isQSTileAdded = false,
                showBridgeButton = true,
                showLaunchAppsWhenBridgeButtonCollapsed = false,
            ),
            developmentSectionState = SettingsScreen2DevelopmentSectionState(
                isExportDisabled = false,
            )
        )
    }
}