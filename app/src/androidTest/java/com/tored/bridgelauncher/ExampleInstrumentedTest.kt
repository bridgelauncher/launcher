package com.tored.bridgelauncher

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tored.bridgelauncher.services.iconpacks.AppFilterXMLParsingResult
import com.tored.bridgelauncher.services.iconpacks.parseAppFilterXML
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.xmlpull.v1.XmlPullParserFactory

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest
{
    @Test
    fun parse_appstract()
    {
        val result = parse_iconPackXML("appstract")
        assertNotNull(result)
    }

    @Test
    fun parse_cuscon()
    {
        parse_iconPackXML("cuscon")
    }

    @Test
    fun parse_raphael()
    {
        parse_iconPackXML("raphael")
    }

    @Test
    fun parse_s60()
    {
        parse_iconPackXML("s60")
    }

    fun parse_iconPackXML(name: String): AppFilterXMLParsingResult
    {
        InstrumentationRegistry.getInstrumentation().context.assets.open("appfilters/$name-appfilter.xml").use { file ->

            assertNotNull(file)

            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(file, "utf-8")
            return parseAppFilterXML(parser)
        }
    }
}