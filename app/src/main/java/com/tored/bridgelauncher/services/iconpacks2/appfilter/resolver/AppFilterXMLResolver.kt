package com.tored.bridgelauncher.services.iconpacks2.appfilter.resolver

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.content.res.AssetManager
import android.content.res.Resources
import android.util.Xml
import com.tored.bridgelauncher.utils.xmlPullParserFor
import org.xmlpull.v1.XmlPullParser
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

data class AppFilterXMLLocations(
    val resXml: Boolean,
    val resRaw: Boolean,
    val assets: Boolean,
)

data class XmlPullParserAndStream(
    val parser: XmlPullParser,
    val stream: InputStream? = null,
)

class AppFilterXMLResolver(
    private val _pm: PackageManager,
)
{
    private fun resOrNull(packageName: String): Resources?
    {
        return try
        {
            _pm.getResourcesForApplication(packageName)
        }
        catch (_: NameNotFoundException)
        {
            null
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun Resources.getIdentifierOrNull(name: String, defType: String): Int?
    {
        val id = getIdentifier(name, defType, null)
        return if (id <= 0) null else id
    }

    fun Resources.getAppFilterResXMLIdOrNull(packageName: String): Int?
    {
        return getIdentifierOrNull("appfilter.xml", "xml")
    }

    fun Resources.getAppFilterResRawIdOrNull(packageName: String): Int?
    {
        return getIdentifierOrNull("appfilter.xml", "raw")
    }

    fun Resources.getIsAppFilterInAssets(packageName: String): Boolean
    {
        return assets?.exists("appfilter.xml") ?: false
    }


    fun InputStream.toXmlPullParserAndStream() = XmlPullParserAndStream(
        parser = xmlPullParserFor(this),
        stream = this,
    )

    fun XmlPullParser.toXmlPullParserWithoutStream() = XmlPullParserAndStream(
        parser = this,
        stream = null,
    )

    fun getAppFilterXMLLocations(packageName: String): AppFilterXMLLocations?
    {
        return resOrNull(packageName)?.run {
            AppFilterXMLLocations(
                resXml = getAppFilterResXMLIdOrNull(packageName) != null,
                resRaw = getAppFilterResRawIdOrNull(packageName) != null,
                assets = getIsAppFilterInAssets(packageName),
            )
        }
    }


    fun tryGetAppFilterResXmlParser(packageName: String): XmlPullParser?
    {
        return resOrNull(packageName)?.let { res ->
            res.getAppFilterResXMLIdOrNull(packageName)?.let { resId ->
                res.getXml(resId)
            }
        }
    }

    /**
     * Reads appfilter from res/xml using an XmlPullParser and recreates the original appfilter.xml file.
     * This is needed because XML resources are pre-parsed at build time and so we can't easily access their raw XML form.
     */
    fun tryGetAppFilterResXmlStream(packageName: String): InputStream?
    {
        return tryGetAppFilterResXmlParser(packageName)?.let { parser ->

            val stream = ByteArrayOutputStream()
            val serializer = Xml.newSerializer()
            serializer.setOutput(stream, "utf8")

            while (true)
            {
                when (parser.next())
                {
                    XmlPullParser.START_TAG ->
                    {
                        serializer.startTag(parser.namespace, parser.name)
                        for (i in 0..<parser.attributeCount)
                            serializer.attribute(parser.getAttributeNamespace(i), parser.getAttributeName(i), parser.getAttributeValue(i))
                    }

                    XmlPullParser.TEXT -> serializer.text(parser.text)
                    XmlPullParser.END_TAG -> serializer.endTag(parser.namespace, parser.name)
                    XmlPullParser.END_DOCUMENT -> break
                }
            }

            serializer.flush()
            stream.toByteArray().inputStream()
        }
    }

    fun tryGetAppFilterResRawStream(packageName: String): InputStream?
    {
        return resOrNull(packageName)?.let { res ->
            res.getAppFilterResRawIdOrNull(packageName)?.let { resId ->
                res.openRawResource(resId)
            }
        }
    }

    fun tryGetAppFilterAssetsStream(packageName: String): InputStream?
    {
        return resOrNull(packageName)?.let { res ->
            res.assets?.open("appfilter.xml")
        }
    }


    fun resolveAppFilterParser(packageName: String, source: AppFilterXMLSourceOptions): XmlPullParserAndStream?
    {
        return when (source)
        {
            AppFilterXMLSourceOptions.ResXML -> tryGetAppFilterResXmlParser(packageName)?.toXmlPullParserWithoutStream()
            AppFilterXMLSourceOptions.ResRaw -> tryGetAppFilterResRawStream(packageName)?.toXmlPullParserAndStream()
            AppFilterXMLSourceOptions.Assets -> tryGetAppFilterAssetsStream(packageName)?.toXmlPullParserAndStream()
            AppFilterXMLSourceOptions.Auto ->
            {
                tryGetAppFilterResXmlParser(packageName)?.toXmlPullParserWithoutStream()
                    ?: tryGetAppFilterResRawStream(packageName)?.toXmlPullParserAndStream()
                    ?: tryGetAppFilterAssetsStream(packageName)?.toXmlPullParserAndStream()
            }
        }
    }

    fun resolveRawAppFilterXMLStream(packageName: String, source: AppFilterXMLSourceOptions): InputStream?
    {
        return when (source)
        {
            AppFilterXMLSourceOptions.ResXML -> tryGetAppFilterResXmlStream(packageName)
            AppFilterXMLSourceOptions.ResRaw -> tryGetAppFilterResRawStream(packageName)
            AppFilterXMLSourceOptions.Assets -> tryGetAppFilterAssetsStream(packageName)
            AppFilterXMLSourceOptions.Auto ->
            {
                tryGetAppFilterResXmlStream(packageName)
                    ?: tryGetAppFilterResRawStream(packageName)
                    ?: tryGetAppFilterAssetsStream(packageName)
            }
        }
    }
}

fun AssetManager.exists(filename: String): Boolean
{
    var s: InputStream? = null
    return try
    {
        s = open(filename)
        true
    }
    catch (_: IOException)
    {
        false
    }
    finally
    {
        s?.close()
    }
}