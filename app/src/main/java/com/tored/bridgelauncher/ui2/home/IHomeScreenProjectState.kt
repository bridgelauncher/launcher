package com.tored.bridgelauncher.ui2.home

import androidx.compose.runtime.Immutable
import java.io.File

sealed interface IHomeScreenProjectState
{
    @Immutable
    data object FirstTimeLaunch : IHomeScreenProjectState

    @Immutable
    data object Initializing : IHomeScreenProjectState

    @Immutable
    data object NoStoragePerm : IHomeScreenProjectState

    @Immutable
    data object NoProjectLoaded : IHomeScreenProjectState

    @Immutable
    data class ProjectLoaded(
        val currentProjectDir: File,
    ) : IHomeScreenProjectState
}