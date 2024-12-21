package com.tored.bridgelauncher.services.iconpacks2.appfilter.parser

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.Log
import com.tored.bridgelauncher.services.iconpacks2.appfilter.parser.AppFilterXMLParsingAttemptResult.ParsedAppFilterXML
import com.tored.bridgelauncher.utils.isNotNullOrBlank
import com.tored.bridgelauncher.utils.skipToEndTag
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException

class AppFilterXMLParser(
    private val _pm: PackageManager,
)
{
    @Suppress("PrivatePropertyName")
    private val TAG = AppFilterXMLParser::class.simpleName

    private val ns: String? = null

    private fun getResources(packageName: String) = _pm.getResourcesForApplication(packageName)

    @SuppressLint("DiscouragedApi") // resources.getIdentifier is required for this to function
    suspend fun attemptToParseAppFilterXML(
        packageName: String,
    ): AppFilterXMLParsingAttemptResult
    {
        val resources = getResources(packageName)

        var rawAppFilterXML: String? = null

        val parser = try
        {
            // by default, try to open appfilter.xml from assets (because we want to read the raw XML as well)
            val stream = resources.assets.open("appfilter.xml")

            Log.d(TAG, "attemptToParseAppFilterXML: $packageName: using appfilter.xml from assets")

            XmlPullParserFactory.newInstance().newPullParser().apply {
                setInput(stream, null) // detect encoding automatically
            }
        }
        catch (_: IOException)
        {
            // couldn't open from assets, fall back to using an XML resource (can't read raw XML this way)
            when (val resId = resources.getIdentifier("appfilter", "xml", packageName))
            {
                0 -> return AppFilterXMLParsingAttemptResult.CouldNotParseAppFilterXML(CouldNotParseAppFilterXMLReason.AppFilterXMLNotFound)
                else ->
                {
                    try
                    {
                        resources.getXml(resId).also {
                            Log.d(TAG, "attemptToParseAppFilterXML: $packageName: using appfilter.xml from resources/xml")
                        }
                    }
                    catch (_: Resources.NotFoundException)
                    {
                        return AppFilterXMLParsingAttemptResult.CouldNotParseAppFilterXML(CouldNotParseAppFilterXMLReason.AppFilterXMLNotFound)
                    }
                }
            }
        }

        var scaleFactor = 1f
        val iconBackImgs = mutableMapOf<Int, String>()
        var iconMaskImg: String? = null
        var iconUponImg: String? = null
        // component name to drawable map
        val items = mutableMapOf<String, String>()
        // component name to prefix map
        val calendars = mutableMapOf<String, String>()
        val dynamicClocks = mutableMapOf<String, String>()

        parser.next()
        parser.require(XmlPullParser.START_TAG, ns, "resources")

        fun parsingWarning(text: String)
        {
            Log.w(TAG, buildString {
                appendLine("$packageName: $text near ${parser.lineNumber}:${parser.columnNumber}")

                append("<${parser.name}")
                for (i in 0..<parser.attributeCount)
                {
                    append(" ")
                    append(parser.getAttributeName(i))
                    append("=\"")
                    append(parser.getAttributeValue(i))
                    append("\"")
                }
                append(">")
            })
        }

        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.eventType != XmlPullParser.START_TAG)
                continue

            when (parser.name)
            {
                "iconback" ->
                {
                    // multiple iconbacks can be probided in the iconback element like <iconback img1="..." img2="..." ... />
                    val attrCount = parser.attributeCount

                    for (i in 0..<attrCount - 1)
                    {
                        val attrName = parser.getAttributeName(i)
                        if (attrName.startsWith("img"))
                        {
                            val numStr = attrName.substringAfter("img")
                            val num = numStr.toIntOrNull()
                            if (num != null)
                                iconBackImgs[num] = parser.getAttributeValue(i)
                        }
                    }
                }

                "iconmask" ->
                {
                    val img1 = parser.getAttributeValue(null, "img1")
                    if (img1.isNotNullOrBlank())
                        iconMaskImg = img1
                    else
                        parsingWarning("<iconmask> with missing or empty img1 attribute")
                }

                "iconupon" ->
                {
                    val img1 = parser.getAttributeValue(null, "img1")
                    if (img1.isNotNullOrBlank())
                        iconUponImg = img1
                    else
                        parsingWarning("<iconupon> with missing or empty img1 attribute")
                }

                "scale" ->
                {
                    val factorStr = parser.getAttributeValue(null, "factor")
                    val factor = factorStr.toFloatOrNull()
                    if (factor != null)
                        scaleFactor = factor
                    else
                        parsingWarning("<scale> with missing, empty or non-parseable factor attribute (factor = $factorStr)")
                }

                "item" ->
                {
                    val component = parser.getAttributeValue(null, "component")
                    val drawable = parser.getAttributeValue(null, "drawable")
                    if (component.isNotNullOrBlank() && drawable.isNotNullOrBlank())
                        items[component] = drawable
                    else
                        parsingWarning("<item> with missing or empty component and/or drawable attribute (component = $component, drawable = $drawable)")
                }

                "calendar" ->
                {
                    val component = parser.getAttributeValue(null, "component")
                    val prefix = parser.getAttributeValue(null, "prefix")
                    if (component.isNotNullOrBlank() && prefix.isNotNullOrBlank())
                        calendars[component] = prefix
                    else
                        parsingWarning("<calendar> with missing or empty component and/or prefix attribute (component = $component, prefix = $prefix)")
                }

                else ->
                {
                    parsingWarning("unexpected tag")
                }
            }

            parser.skipToEndTag()
        }

        return ParsedAppFilterXML(
            rawXML = null,
            scaleFactor = scaleFactor,
            iconBackImgs = iconBackImgs,
            iconMaskImg = iconMaskImg,
            iconUponImg = iconUponImg,
            items = items,
            calendars = calendars,
            dynamicClocks = dynamicClocks,
        )
    }
}