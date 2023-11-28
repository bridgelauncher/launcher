package com.tored.bridgelauncher.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import androidx.compose.runtime.Composable
import com.tored.bridgelauncher.annotations.Display
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation

fun getIsExtStorageManager(): Boolean
{
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.R
            // we need a special permission on Android 11 and up
            || Environment.isExternalStorageManager()
}

fun Context.startExtStorageManagerPermissionActivity(): Unit
{
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager())
    {
        try
        {
            startActivity(
                Intent(
                    android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    android.net.Uri.parse("package:${packageName}")
                )
            )
        }
        catch (ex: Exception)
        {
            android.widget.Toast.makeText(this, "Could not navigate to settings to grant access to all files.", android.widget.Toast.LENGTH_LONG).show()
        }
    }
}

typealias ComposableContent = @Composable () -> Unit

fun <TClass, TProp> displayNameFor(prop: KProperty1<TClass, TProp>): String
{
    val ann = prop.findAnnotation<Display>()
    return ann?.name ?: prop.name
}