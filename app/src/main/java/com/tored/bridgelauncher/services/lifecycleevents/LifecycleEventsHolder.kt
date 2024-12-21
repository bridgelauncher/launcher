package com.tored.bridgelauncher.services.lifecycleevents

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

private val TAG = LifecycleEventsHolder::class.simpleName

class LifecycleEventsHolder
{
    private val _homeScreenBeforePause = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val homeScreenBeforePause = _homeScreenBeforePause.asSharedFlow()
    fun notifyHomeScreenPaused() = _homeScreenBeforePause.tryEmit(Unit)

    private val _homeScreenNewIntent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val homeScreenNewIntent = _homeScreenNewIntent.asSharedFlow()
    fun notifyHomeScreenReceivedNewIntent() = _homeScreenNewIntent.tryEmit(Unit)

    private val _homeScreenAfterResume = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val homeScreenAfterResume = _homeScreenAfterResume.asSharedFlow()
    fun notifyHomeScreenResumed() = _homeScreenAfterResume.tryEmit(Unit)
}