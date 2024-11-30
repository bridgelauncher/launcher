package com.tored.bridgelauncher.services.windowinsetsholder

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WindowInsetsHolder
{
    private val _stateFlowMap = WindowInsetsOptions.entries.associateWith {
        MutableStateFlow(WindowInsetsSnapshot.zero())
    }

    val stateFlowMap = _stateFlowMap as Map<WindowInsetsOptions, StateFlow<WindowInsetsSnapshot>>

    val notifyWindowInsetsChanged: OnWindowInsetsChangedFunc = { option, snapshot ->
        _stateFlowMap[option]!!.value = snapshot
    }
}