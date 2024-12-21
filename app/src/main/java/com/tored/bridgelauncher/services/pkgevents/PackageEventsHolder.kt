package com.tored.bridgelauncher.services.pkgevents

import com.tored.bridgelauncher.services.system.BridgeLauncherBroadcastReceiver
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Holds SharedFlows for relevant package events.
 * Is notified about those events by [BridgeLauncherBroadcastReceiver].
 */
class PackageEventsHolder
{
    // if extraBufferCapacity is not set to 1, tryEmit simply doesn't work
    private val _packageAddedEvents = MutableSharedFlow<PackageEvent.Added>(extraBufferCapacity = 1)
    val packageAddedEvents = _packageAddedEvents.asSharedFlow()
    fun notifyPackageAdded(packageName: String) = _packageAddedEvents.tryEmit(PackageEvent.Added(packageName))

    // if extraBufferCapacity is not set to 1, tryEmit simply doesn't work
    private val _packageReplacedEvents = MutableSharedFlow<PackageEvent.Replaced>(extraBufferCapacity = 1)
    val packageReplacedEvents = _packageReplacedEvents.asSharedFlow()
    fun notifyPackageReplaced(packageName: String) = _packageReplacedEvents.tryEmit(PackageEvent.Replaced(packageName))

    // if extraBufferCapacity is not set to 1, tryEmit simply doesn't work
    private val _packageRemovedEvents = MutableSharedFlow<PackageEvent.Removed>(extraBufferCapacity = 1)
    val packageRemovedEvents = _packageRemovedEvents.asSharedFlow()
    fun notifyPackageRemoved(packageName: String) = _packageRemovedEvents.tryEmit(PackageEvent.Removed(packageName))
}