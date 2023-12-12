package com.tored.bridgelauncher

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import com.tored.bridgelauncher.ui.screens.settings.SettingsScreen
import com.tored.bridgelauncher.ui.theme.BridgeLauncherTheme
import com.tored.bridgelauncher.utils.hasStoragePerms
import com.tored.bridgelauncher.utils.startExtStorageManagerPermissionActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : ComponentActivity()
{
    private var _hasStoragePerms by mutableStateOf(false)
    private val reqStoragePermsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    )
    { grantedMap ->
        _hasStoragePerms = grantedMap.values.all { it }
        if (!_hasStoragePerms)
            Toast.makeText(this, "Storage permissions are necessary for Bridge to function.", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        _hasStoragePerms = hasStoragePerms()

        setContent {
            BridgeLauncherTheme()
            {
                SettingsScreen(
                    _hasStoragePerms,
                    onGrantPermissionRequest = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                        {
                            startExtStorageManagerPermissionActivity()
                        }
                        else
                        {
                            if (
                                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            )
                            {
                                _hasStoragePerms = true
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
        _hasStoragePerms = hasStoragePerms()
    }
}
