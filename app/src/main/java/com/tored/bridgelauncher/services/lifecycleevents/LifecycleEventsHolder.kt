package com.tored.bridgelauncher.services.lifecycleevents

import android.util.Log
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

private val TAG = LifecycleEventsHolder::class.simpleName

class LifecycleEventsHolder
{
    private val _homeScreenBeforePause = Channel<Unit> {  }
    val homeScreenBeforePause = _homeScreenBeforePause.receiveAsFlow()

    private val _homeScreenNewIntent = Channel<Unit> {  }
    val homeScreenNewIntent = _homeScreenNewIntent.receiveAsFlow()

    private val _homeScreenAfterResume = Channel<Unit> {  }
    val homeScreenAfterResume = _homeScreenAfterResume.receiveAsFlow()

    fun notifyHomeScreenPaused()
    {
        Log.d(TAG, "notifyHomeScreenPaused")
        _homeScreenBeforePause.trySend(Unit)
    }

    fun notifyHomeScreenResumed()
    {
        Log.d(TAG, "notifyHomeScreenResumed")
        _homeScreenAfterResume.trySend(Unit)
    }

    fun notifyHomeScreenReceivedNewIntent()
    {
        Log.d(TAG, "notifyHomeScreenReceivedNewIn")
        _homeScreenNewIntent.trySend(Unit)
    }
}