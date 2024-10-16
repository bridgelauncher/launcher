package com.tored.bridgelauncher

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.tored.bridgelauncher.ui.settings.SettingsScreen
import com.tored.bridgelauncher.ui.theme.BridgeLauncherTheme
import com.tored.bridgelauncher.utils.CurrentAndroidVersion
import com.tored.bridgelauncher.utils.checkStoragePerms
import com.tored.bridgelauncher.utils.tryStartExtStorageManagerPermissionActivity

class SettingsActivity : ComponentActivity()
{
    private lateinit var _bridge: BridgeLauncherApplication

    private val reqStoragePermsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    )
    { grantedMap ->
        _bridge.hasStoragePerms = grantedMap.values.all { it }
        if (!_bridge.hasStoragePerms)
            Toast.makeText(this, "Storage permissions are necessary for Bridge to function.", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        _bridge  = applicationContext as BridgeLauncherApplication
        _bridge.hasStoragePerms = checkStoragePerms()

        setContent {
            BridgeLauncherTheme()
            {
                SettingsScreen(
                    _bridge.hasStoragePerms,
                    onGrantPermissionRequest = {
                        if (CurrentAndroidVersion.supportsScopedStorage())
                        {
                            tryStartExtStorageManagerPermissionActivity()
                        }
                        else
                        {
                            if (
                                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            )
                            {
                                _bridge.hasStoragePerms = true
                            }
                            else
                            {
                                reqStoragePermsLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    )
                                )
                            }
                        }
                    }
                )
            }
        }
    }

    override fun onResume()
    {
        super.onResume()
        _bridge.hasStoragePerms = checkStoragePerms()
    }
}
