package com.tored.bridgelauncher.utils

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

// https://developer.android.com/develop/connectivity/network-ops/xml#skip
@Throws(XmlPullParserException::class, IOException::class)
fun XmlPullParser.skipToEndTag()
{
    if (eventType != XmlPullParser.START_TAG)
    {
        throw IllegalStateException()
    }

    var depth = 1
    while (depth != 0)
    {
        when (next())
        {
            XmlPullParser.END_TAG -> depth--
            XmlPullParser.START_TAG -> depth++
        }
    }
}