@file:JvmMultifileClass
@file:JvmName("JSToBridgeAPI")

package com.tored.bridgelauncher.api2.jstobridge

import com.tored.bridgelauncher.api2.jstobridge.internal.JSToBridgeAPIDeps
import com.tored.bridgelauncher.api2.jstobridge.internal.JSToBridgeAPI_WindowInsets
import com.tored.bridgelauncher.api2.jstobridge.internal._JSToBridgeAPI_Base

/** Object to be injected into the WebView as the global variable `Bridge`. */
class JSToBridgeAPI(deps: JSToBridgeAPIDeps) : JSToBridgeAPI_WindowInsets(deps)
{
    /** @see [_JSToBridgeAPI_Base] if you're wondering why there is no class body here. */
}

