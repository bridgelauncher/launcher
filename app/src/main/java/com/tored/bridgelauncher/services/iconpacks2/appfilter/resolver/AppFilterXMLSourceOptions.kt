package com.tored.bridgelauncher.services.iconpacks2.appfilter.resolver

import com.tored.bridgelauncher.utils.RawRepresentable

enum class AppFilterXMLSourceOptions(override val rawValue: String) : RawRepresentable<String>
{
    ResXML("res/xml"),
    ResRaw("res/raw"),
    Assets("assets"),
    Auto("auto"),
}