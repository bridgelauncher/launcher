package com.tored.bridgelauncher.services.iconpacks

import com.tored.bridgelauncher.utils.skipToEndTag
import org.xmlpull.v1.XmlPullParser

private val ns: String? = null

data class AppFilterXMLParsingResult(
    val scaleFactor: Float,
    val iconBackImgs: Map<Int, String>,
    val iconMaskImg: String?,
    val iconUponImg: String?,
    val items: Map<String, String>,
)

fun parseAppFilterXML(parser: XmlPullParser): AppFilterXMLParsingResult
{
    var scaleFactor = 1f
    val iconBackImgs = mutableMapOf<Int, String>()
    var iconMaskImg: String? = null
    var iconUponImg: String? = null
    val items = mutableMapOf<String, String>()

    parser.next()
    parser.require(XmlPullParser.START_TAG, ns, "resources")

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

                // suppressing because rangeUntil doesn't work, probably outdated Kotlin version? I don't care
                @Suppress("ReplaceRangeToWithRangeUntil")
                for (i in 0..attrCount - 1)
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
                if (img1.isNotBlank())
                    iconMaskImg = img1
            }

            "iconupon" ->
            {
                val img1 = parser.getAttributeValue(null, "img1")
                if (img1.isNotBlank())
                    iconUponImg = img1
            }

            "scale" ->
            {
                val factorStr = parser.getAttributeValue(null, "factor")
                val factor = factorStr.toFloatOrNull()
                if (factor != null)
                    scaleFactor = factor
            }

            "item" ->
            {
                val component = parser.getAttributeValue(null, "component")
                val drawable = parser.getAttributeValue(null, "drawable")
                if (component.isNotBlank() && drawable.isNotBlank())
                    items[component] = drawable
            }

            else ->
            {
            }

        }

        parser.skipToEndTag()
    }

    return AppFilterXMLParsingResult(
        scaleFactor,
        iconBackImgs,
        iconMaskImg,
        iconUponImg,
        items,
    )
}
