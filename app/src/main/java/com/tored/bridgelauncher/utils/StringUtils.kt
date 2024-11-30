package com.tored.bridgelauncher.utils

fun String?.defaultIfNullOrEmpty(default: String): String
{
    return if (isNullOrEmpty()) default else this
}

/** Quote - wraps the given string in quotation marks. */
fun q(s: Any?) = "\"$s\""
fun q(s: RawRepresentable<String>) = "\"${s.rawValue}\""