package com.tored.bridgelauncher.services.iconpacks2.appfilter.parser

import kotlinx.serialization.Serializable

sealed interface AppFilterXMLParsingAttemptResult
{
    data class CouldNotParseAppFilterXML(
        val reason: CouldNotParseAppFilterXMLReason,
    ) : AppFilterXMLParsingAttemptResult

    @Serializable
    data class ParsedAppFilterXML(
        val rawXML: String?,
        val scaleFactor: Float,
        val iconBackImgs: Map<Int, String>,
        val iconMaskImg: String?,
        val iconUponImg: String?,
        /** <item> component name to drawable map */
        val items: Map<String, String>,
        /** <calendar> component name to prefix map */
        val calendars: MutableMap<String, String>,
        val dynamicClocks: MutableMap<String, String>,
    ) : AppFilterXMLParsingAttemptResult
}