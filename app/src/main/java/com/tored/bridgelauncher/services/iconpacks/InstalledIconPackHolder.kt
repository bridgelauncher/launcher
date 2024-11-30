package com.tored.bridgelauncher.services.iconpacks

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.InputStream

class InstalledIconPackHolder(
    private val _pm: PackageManager,
    val packageName: String
)
{
    // Resources for this icon pack app. Should be refreshed whenever the app is modified just in case.
    private var resources: Resources? = null

    // The result of parsing this icon pack's appfilter.xml. Should be refreshed whenever the app is modified.
    private var _appFilterParsingResultCache: AppFilterXMLParsingResult? = null
    private val _appFilterParsingMutex = Mutex()
    suspend fun getAppFilterParsingResult(): AppFilterXMLParsingResult
    {
        // return immediately if icon packs are already loaded and cached
        return _appFilterParsingResultCache ?: kotlin.run()
        {
            // mutex ensures the first thread to get here parses the appfilter.xml, the rest wait for the lock to be released
            // when another thread enters here, the appfilter parsing result will already be available and thus it'll exit immediately
            _appFilterParsingMutex.withLock()
            {
                if (_appFilterParsingResultCache == null)
                    _appFilterParsingResultCache = parseAppFilterXML()
            }

            // we're past the mutex which must mean the appfilter.xml must be parsed
            return _appFilterParsingResultCache!!
        }
    }

    private fun getResources(): Resources
    {
        return resources ?: _pm.getResourcesForApplication(packageName).also {
            resources = it
        }
    }

    fun parseAppFilterXML(): AppFilterXMLParsingResult
    {
        TODO()
    }

    suspend fun getIconForOrNull(appPackageName: String): Drawable?
    {
        return getAppFilterParsingResult().items[appPackageName]?.let { getDrawableOrNull(it) }
    }

    fun getDrawableOrNull(name: String): Drawable?
    {
        val resources = getResources()

        @SuppressLint("DiscouragedApi") // I only have a name, I need an id, don't yell at me :)
        val id = resources.getIdentifier(name, "drawable", packageName)

        return if (id > 0)
            ResourcesCompat.getDrawable(resources, id, null)
        else
            null
    }

    // this initially preferred trying to get the appfilter.xml from resources instead of assets
    // but it turned out calling openRawResource on a resource in res/xml results in a binary mess because the XML gets compiled
    // so now we're relying on getting the appfilter.xml from assets
    // if https://github.com/jahirfiquitiva/Blueprint/wiki/How-to-create-and-setup-an-icon-pack is to be believed,
    // icon pack devs are recommended to include a copy of appfilter.xml in assets anyway, so we should be good
    fun getRawAppFilterXmlStream(): InputStream = getResources().assets.open("appfilter.xml")
}