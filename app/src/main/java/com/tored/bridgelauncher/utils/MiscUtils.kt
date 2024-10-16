package com.tored.bridgelauncher.utils

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
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

fun Context.showErrorToast(ex: Exception)
{
    showErrorToast(ex.messageOrDefault())
}

fun Context.showErrorToast(message: String?)
{
    Toast.makeText(this, message ?: "Exception with no message.", Toast.LENGTH_LONG).show()
}

fun Context.getIsSystemInNightMode(): Boolean
{
    return if (CurrentAndroidVersion.supportsNightMode())
    {
        resources.configuration.isNightModeActive
    }
    else
    {
        resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}

/** Quote - wraps the given string in quotation marks. */
fun q(s: String?) = "\"$s\""

object EncodingStrings
{
    const val UTF8 = "utf-8"
}