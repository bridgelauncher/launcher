package com.tored.bridgelauncher.ui2.settings.composables

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tored.bridgelauncher.R
import com.tored.bridgelauncher.services.mockexport.MockExportProgressState
import com.tored.bridgelauncher.services.settings.SystemBarAppearanceOptions
import com.tored.bridgelauncher.services.settings.ThemeOptions
import com.tored.bridgelauncher.ui.theme.BridgeLauncherThemeStateless
import com.tored.bridgelauncher.ui2.dirpicker.DirectoryPickerActions
import com.tored.bridgelauncher.ui2.dirpicker.DirectoryPickerDialog
import com.tored.bridgelauncher.ui2.dirpicker.DirectoryPickerState
import com.tored.bridgelauncher.ui2.progressdialog.MockExportProgressDialog
import com.tored.bridgelauncher.ui2.progressdialog.MockExportProgressDialogActions
import com.tored.bridgelauncher.ui2.settings.SettingsScreen2MiscActions
import com.tored.bridgelauncher.ui2.settings.SettingsScreenVM
import com.tored.bridgelauncher.ui2.settings.sections.about.SettingsScreen2AboutSectionContent
import com.tored.bridgelauncher.ui2.settings.sections.bridge.SettingsScreen2BridgeSectionActions
import com.tored.bridgelauncher.ui2.settings.sections.bridge.SettingsScreen2BridgeSectionContent
import com.tored.bridgelauncher.ui2.settings.sections.bridge.SettingsScreen2BridgeSectionState
import com.tored.bridgelauncher.ui2.settings.sections.development.SettingsScreen2DevelopmentSectionActions
import com.tored.bridgelauncher.ui2.settings.sections.development.SettingsScreen2DevelopmentSectionContent
import com.tored.bridgelauncher.ui2.settings.sections.development.SettingsScreen2DevelopmentSectionState
import com.tored.bridgelauncher.ui2.settings.sections.overlays.SettingsScreen2OverlaysSectionActions
import com.tored.bridgelauncher.ui2.settings.sections.overlays.SettingsScreen2OverlaysSectionContent
import com.tored.bridgelauncher.ui2.settings.sections.overlays.SettingsScreen2OverlaysSectionState
import com.tored.bridgelauncher.ui2.settings.sections.project.ScreenLockingMethodOptions
import com.tored.bridgelauncher.ui2.settings.sections.project.SettingsScreen2ProjectSectionActions
import com.tored.bridgelauncher.ui2.settings.sections.project.SettingsScreen2ProjectSectionContent
import com.tored.bridgelauncher.ui2.settings.sections.project.SettingsScreen2ProjectSectionState
import com.tored.bridgelauncher.ui2.settings.sections.project.SettingsScreen2ProjectSectionStateProjectInfo
import com.tored.bridgelauncher.ui2.settings.sections.wallpaper.SettingsScreen2WallpaperSectionActions
import com.tored.bridgelauncher.ui2.settings.sections.wallpaper.SettingsScreen2WallpaperSectionContent
import com.tored.bridgelauncher.ui2.settings.sections.wallpaper.SettingsScreen2WallpaperSectionState
import com.tored.bridgelauncher.ui2.shared.BotBarScreen
import com.tored.bridgelauncher.utils.CurrentAndroidVersion
import com.tored.bridgelauncher.utils.UseEdgeToEdgeWithTransparentBars
import com.tored.bridgelauncher.utils.tryStartExtStorageManagerPermissionActivity

@Composable
fun SettingsScreen2(vm: SettingsScreenVM = viewModel(), requestFinish: () -> Unit)
{
    SettingsScreen2(
        projectSectionState = vm.projectSectionState.value,
        projectSectionActions = vm.projectSectionActions,

        wallpaperSectionState = vm.wallpaperSectionState.value,
        wallpaperSectionActions = vm.wallpaperSectionActions,

        overlaysSectionState = vm.overlaysSectionState.value,
        overlaysSectionActions = vm.overlaysSectionActions,

        bridgeSectionState = vm.bridgeSectionState.value,
        bridgeSectionActions = vm.bridgeSectionActions,

        developmentSectionState = vm.developmentSectionState.value,
        developmentSectionActions = vm.developmentSectionActions,

        directoryPickerState = vm.directoryPickerState.value,
        directoryPickerActions = vm.directoryPickerActions,

        mockExportProgressState = vm.mockExportProgressState.value,
        mockExportProgressDialogActions = vm.mockExportProgressDialogActions,

        miscActions = vm.miscActions,
        requestFinish = requestFinish,
    )
}

@Composable
fun SettingsScreen2(
    projectSectionState: SettingsScreen2ProjectSectionState,
    projectSectionActions: SettingsScreen2ProjectSectionActions,

    wallpaperSectionState: SettingsScreen2WallpaperSectionState,
    wallpaperSectionActions: SettingsScreen2WallpaperSectionActions,

    overlaysSectionState: SettingsScreen2OverlaysSectionState,
    overlaysSectionActions: SettingsScreen2OverlaysSectionActions,

    bridgeSectionState: SettingsScreen2BridgeSectionState,
    bridgeSectionActions: SettingsScreen2BridgeSectionActions,

    developmentSectionState: SettingsScreen2DevelopmentSectionState,
    developmentSectionActions: SettingsScreen2DevelopmentSectionActions,

    directoryPickerState: DirectoryPickerState?,
    directoryPickerActions: DirectoryPickerActions,

    mockExportProgressState: MockExportProgressState?,
    mockExportProgressDialogActions: MockExportProgressDialogActions,

    miscActions: SettingsScreen2MiscActions,
    requestFinish: () -> Unit,
)
{
    val permsLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) { areGranted ->
        miscActions.permissionsChanged(areGranted)
    }

    val context = LocalContext.current

    fun requestStoragePermission()
    {
        if (CurrentAndroidVersion.supportsScopedStorage())
        {
            context.tryStartExtStorageManagerPermissionActivity()
        }
        else
        {
            permsLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                )
            )
        }
    }

    UseEdgeToEdgeWithTransparentBars()

    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize(),
    )
    {
        BotBarScreen(
            onLeftActionClick = { requestFinish() },
            titleAreaContent = {
                Text(text = "Settings")
            }
        )
        {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .safeContentPadding()
                    .padding(0.dp, 8.dp)
            )
            {
                SettingsScreen2Section(label = "Project", iconResId = R.drawable.ic_folder_open) {
                    SettingsScreen2ProjectSectionContent(
                        state = projectSectionState,
                        actions = projectSectionActions,
                        requestStoragePermission = ::requestStoragePermission,
                    )
                }

                Divider()

                SettingsScreen2Section(label = "System wallpaper", iconResId = R.drawable.ic_image) {
                    SettingsScreen2WallpaperSectionContent(
                        state = wallpaperSectionState,
                        actions = wallpaperSectionActions,
                    )
                }

                Divider()

                SettingsScreen2Section(label = "Overlays", iconResId = R.drawable.ic_overlays) {
                    SettingsScreen2OverlaysSectionContent(
                        state = overlaysSectionState,
                        actions = overlaysSectionActions,
                    )
                }

                Divider()

                SettingsScreen2Section(label = "Bridge", iconResId = R.drawable.ic_bridge) {
                    SettingsScreen2BridgeSectionContent(
                        state = bridgeSectionState,
                        actions = bridgeSectionActions,
                    )
                }

                Divider()

                SettingsScreen2Section(label = "Development", iconResId = R.drawable.ic_tools) {
                    SettingsScreen2DevelopmentSectionContent(
                        state = developmentSectionState,
                        actions = developmentSectionActions,
                    )
                }

                Divider()

                SettingsScreen2Section(label = "About Bridge Launcher", iconResId = R.drawable.ic_about) {
                    SettingsScreen2AboutSectionContent()
                }
            }
        }
    }

    if (directoryPickerState != null)
    {
        DirectoryPickerDialog(
            state = directoryPickerState,
            actions = directoryPickerActions,
            requestStoragePermission = ::requestStoragePermission,
        )
    }

    if (mockExportProgressState != null)
    {
        MockExportProgressDialog(
            state = mockExportProgressState,
            actions = mockExportProgressDialogActions,
        )
    }
}


// PREVIEWS

@Composable
@PreviewLightDark
fun SettingsScreen2Preview01()
{
    BridgeLauncherThemeStateless {
        SettingsScreen2(
            projectSectionState = SettingsScreen2ProjectSectionState(
                projectInfo = SettingsScreen2ProjectSectionStateProjectInfo("LOL"),
                hasStoragePerms = true,
                allowProjectsToTurnScreenOff = true,
                screenLockingMethod = ScreenLockingMethodOptions.DeviceAdmin,
                canBridgeTurnScreenOff = true,
            ),
            projectSectionActions = SettingsScreen2ProjectSectionActions.empty(),

            wallpaperSectionState = SettingsScreen2WallpaperSectionState(
                drawSystemWallpaperBehindWebView = true,
            ),
            wallpaperSectionActions = SettingsScreen2WallpaperSectionActions.empty(),

            overlaysSectionState = SettingsScreen2OverlaysSectionState(
                statusBarAppearance = SystemBarAppearanceOptions.Hide,
                navigationBarAppearance = SystemBarAppearanceOptions.LightIcons,
                drawWebViewOverscrollEffects = true,
            ),
            overlaysSectionActions = SettingsScreen2OverlaysSectionActions.empty(),

            bridgeSectionState = SettingsScreen2BridgeSectionState(
                theme = ThemeOptions.System,
                isQSTilePromptSupported = true,
                isQSTileAdded = false,
                showBridgeButton = true,
                showLaunchAppsWhenBridgeButtonCollapsed = false,
            ),
            bridgeSectionActions = SettingsScreen2BridgeSectionActions.empty(),

            developmentSectionState = SettingsScreen2DevelopmentSectionState(
                isExportDisabled = false,
            ),
            developmentSectionActions = SettingsScreen2DevelopmentSectionActions.empty(),

            directoryPickerState = null,
            directoryPickerActions = DirectoryPickerActions.empty(),

            mockExportProgressState = null,
            mockExportProgressDialogActions = MockExportProgressDialogActions.empty(),

            miscActions = SettingsScreen2MiscActions.empty(),
            requestFinish = {}
        )
    }
}