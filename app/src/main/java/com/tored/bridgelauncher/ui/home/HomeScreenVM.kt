package com.tored.bridgelauncher.ui.home

import androidx.lifecycle.ViewModel
import com.tored.bridgelauncher.api.jsapi.BridgeToJSAPI
import com.tored.bridgelauncher.services.settings.ISettingsStateProvider

class HomeScreenVM(
    val settings: ISettingsStateProvider,
    val bridgeToJSAPI: BridgeToJSAPI,

) : ViewModel()
{

}