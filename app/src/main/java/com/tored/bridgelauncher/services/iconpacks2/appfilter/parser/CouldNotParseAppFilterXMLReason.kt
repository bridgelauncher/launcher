package com.tored.bridgelauncher.services.iconpacks2.appfilter.parser

import com.tored.bridgelauncher.utils.RawRepresentable

enum class CouldNotParseAppFilterXMLReason(override val rawValue: String): RawRepresentable<String>
{
    IconPackNotFound("Icon pack not found"),
    AppFilterXMLNotFound("appfilter.xml not found"),
    AppFilterXMLParsingError("appfilter.xml parsing error"),
}