package com.tored.bridgelauncher.utils

import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Window
import androidx.compose.runtime.Composable

fun <E> MutableList<E>.addAll(vararg items: E)
{
    this.addAll(items)
}

typealias ComposableContent = @Composable () -> Unit

fun Exception.messageOrDefault(): String
{
    return message.defaultIfNullOrEmpty(this.javaClass.name)
}

object EncodingStrings
{
    const val UTF8 = "utf-8"
}

fun Window.setNavigationBarContrastEnforcedIfSupported(enforced: Boolean)
{
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
    {
        isNavigationBarContrastEnforced = enforced
    }
}

fun DisplayMetrics.toPx(x: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, x, this)
