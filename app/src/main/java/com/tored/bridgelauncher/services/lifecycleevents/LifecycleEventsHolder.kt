package com.tored.bridgelauncher.services.lifecycleevents

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LifecycleEventsHolder
{
    private val _homeScreenBeforePause = MutableStateFlow(Unit)
    val homeScreenBeforePause = _homeScreenBeforePause.asStateFlow()

    private val _homeScreenAfterResume = MutableStateFlow(Unit)
    val homeScreenAfterResume = _homeScreenAfterResume.asStateFlow()

    fun notifyHomeScreenPaused()
    {
        _homeScreenBeforePause.value = Unit
    }

    fun notifyHomeScreenResumed()
    {
        _homeScreenAfterResume.value = Unit
    }
}