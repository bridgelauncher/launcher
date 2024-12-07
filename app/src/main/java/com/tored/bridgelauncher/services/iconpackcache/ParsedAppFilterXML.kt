package com.tored.bridgelauncher.services.iconpackcache

data class ParsedAppFilterXML(
    val scaleFactor: Float,
    val iconBackImgs: Map<Int, String>,
    val iconMaskImg: String?,
    val iconUponImg: String?,
    val items: Map<String, String>,
)