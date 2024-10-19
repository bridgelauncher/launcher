package com.tored.bridgelauncher.utils

import android.graphics.Color
import android.os.Build
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.tored.bridgelauncher.annotations.Display
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation

fun <E> MutableList<E>.addAll(vararg items: E)
{
    this.addAll(items)
}

typealias ComposableContent = @Composable () -> Unit

fun <TClass, TProp> displayNameFor(prop: KProperty1<TClass, TProp>): String
{
    val ann = prop.findAnnotation<Display>()
    return ann?.name ?: prop.name
}


fun <TClass, TProp> KProperty1<TClass, TProp>.getDisplayName(): String = displayNameFor(this)


fun Exception.messageOrDefault(): String
{
    return message.defaultIfNullOrEmpty(this.javaClass.name)
}

fun String?.defaultIfNullOrEmpty(default: String): String
{
    return if (isNullOrEmpty()) default else this
}

/** Quote - wraps the given string in quotation marks. */
fun q(s: String?) = "\"$s\""

object EncodingStrings
{
    const val UTF8 = "utf-8"
}

fun ComponentActivity.enableEdgeToEdgeWithTransparentSystemBars()
{
    enableEdgeToEdge(
        SystemBarStyle.auto(
            lightScrim = Color.TRANSPARENT,
            darkScrim = Color.TRANSPARENT,
        )
    )

    window.setNavigationBarContrastEnforcedIfSupported(false)
}

fun Window.setNavigationBarContrastEnforcedIfSupported(enforced: Boolean)
{
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
    {
        isNavigationBarContrastEnforced = enforced
    }
}
