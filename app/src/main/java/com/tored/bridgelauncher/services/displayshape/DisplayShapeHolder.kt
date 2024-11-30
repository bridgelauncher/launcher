package com.tored.bridgelauncher.services.displayshape

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DisplayShapeHolder
{
    private val _displayShapePath = MutableStateFlow<String?>(null)
    val displayShapePath = _displayShapePath.asStateFlow()

    fun notifyDisplayShapePathChanged(it: String?)
    {
        _displayShapePath.value = it
    }


    private val _displayCutoutPath = MutableStateFlow<String?>(null)
    val displayCutoutPath = _displayCutoutPath.asStateFlow()

    fun notifyDisplayCutoutPathChanged(it: String?)
    {
        _displayCutoutPath.value = it
    }

}