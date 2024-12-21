package com.tored.bridgelauncher.utils

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream

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

fun xmlPullParserFor(stream: InputStream): XmlPullParser
{
    val factory = XmlPullParserFactory.newInstance()
    val parser = factory.newPullParser()
    parser.setInput(stream, null)
    return parser
}
