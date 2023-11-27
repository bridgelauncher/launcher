package com.tored.bridgelauncher.utils

import android.net.Uri
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tored.bridgelauncher.ui.directorypicker.Directory
import kotlin.reflect.KProperty1

fun getPrefKeyName(className: String, propName: String) = "${className}.${propName}"

inline fun <reified TParent, TProp> getPrefKeyName(prop: KProperty1<TParent, TProp>) = getPrefKeyName(TParent::class.simpleName ?: "", prop.name)

inline fun <reified TParent, reified TEnum> Preferences.readEnum(prop: KProperty1<TParent, TEnum>, default: TEnum): TEnum
        where TEnum : Enum<TEnum>, TEnum : RawRepresentable<Int>
{
    val key = intPreferencesKey(getPrefKeyName(prop))
    return intToEnumOrDefault(this[key], default)
}

inline fun <reified TParent, reified TEnum> MutablePreferences.writeEnum(prop: KProperty1<TParent, TEnum>, value: TEnum)
        where TEnum : Enum<TEnum>, TEnum : RawRepresentable<Int>
{
    val key = intPreferencesKey(getPrefKeyName(prop))
    this[key] = value.rawValue
}

inline fun <reified TParent> Preferences.readBool(
    prop: KProperty1<TParent, Boolean>, default: Boolean
): Boolean
{
    val key = booleanPreferencesKey(getPrefKeyName(prop))
    return this[key] ?: default
}

inline fun <reified TParent> MutablePreferences.writeBool(
    prop: KProperty1<TParent, Boolean>, value: Boolean
)
{
    val key = booleanPreferencesKey(getPrefKeyName(prop))
    this[key] = value
}

inline fun <reified TParent> Preferences.readUri(
    prop: KProperty1<TParent, Uri?>
): Uri?
{
    val key = stringPreferencesKey(getPrefKeyName(prop))
    val uriStr = get(key)
    return if (uriStr == null)
        null
    else
        Uri.parse(uriStr)
}

inline fun <reified TParent> MutablePreferences.writeUri(
    prop: KProperty1<TParent, Uri?>, value: Uri?
)
{
    val key = stringPreferencesKey(getPrefKeyName(prop))
    if (value == null)
        remove(key)
    else
        set(key, value.toString())
}

inline fun <reified TParent> Preferences.readDir(
    prop: KProperty1<TParent, Directory?>
): Directory?
{
    val key = stringPreferencesKey(getPrefKeyName(prop))
    val pathStr = get(key)
    return if (pathStr == null)
        null
    else
        Directory(pathStr)
}

inline fun <reified TParent> MutablePreferences.writeDir(
    prop: KProperty1<TParent, Directory?>, value: Directory?
)
{
    val key = stringPreferencesKey(getPrefKeyName(prop))
    if (value == null)
        remove(key)
    else
        set(key, value.canonicalPath)
}