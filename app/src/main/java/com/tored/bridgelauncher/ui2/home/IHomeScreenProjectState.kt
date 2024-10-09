package com.tored.bridgelauncher.ui2.home

import androidx.compose.runtime.Immutable
import com.tored.bridgelauncher.ui.dirpicker.Directory

sealed interface IHomeScreenProjectState
{
    @Immutable
    object Initializing : IHomeScreenProjectState

    @Immutable
    object NoStoragePerm : IHomeScreenProjectState

    @Immutable
    object NoProjectLoaded : IHomeScreenProjectState

    @Immutable
    data class ProjectLoaded(
        val currentProjectDir: Directory,
    ) : IHomeScreenProjectState
}