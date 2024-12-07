package com.tored.bridgelauncher.ui2.home

import com.tored.bridgelauncher.services.displayshape.OnPathChangeFunc
import com.tored.bridgelauncher.services.windowinsetsholder.OnWindowInsetsChangedFunc
import com.tored.bridgelauncher.services.windowinsetsholder.emptyOnWIndowInsetsChangedFunc

data class HomeScreenObserverCallbacks(
    val onDisplayShapePathChanged: OnPathChangeFunc,
    val onCutoutPathChanged: OnPathChangeFunc,
    val onWindowInsetsChanged: OnWindowInsetsChangedFunc,
)
{
    companion object
    {
        fun empty() = HomeScreenObserverCallbacks({}, {}, emptyOnWIndowInsetsChangedFunc)
    }
}
