package com.tored.bridgelauncher.ui.directorypicker

import java.io.File

// renaming this because it's stupid that directories are also handled by File
typealias Directory = File

val StartupFileNames = setOf(
    "index.html"
)

sealed interface DirectoryPickerUIState
{
    object NoPermission : DirectoryPickerUIState

    class HasPermission(val currentDir: Directory) : DirectoryPickerUIState
    {
        val upDir: Directory?
        val subdirs: List<Directory>
        val startupSubfiles: List<File>
        val regularSubfiles: List<File>

        init
        {
            val parentDir = currentDir.parentFile
            if (parentDir != null && parentDir.canRead())
                this.upDir = parentDir
            else
                this.upDir = null

            val entries = currentDir.listFiles()!!.sortedBy { it.name }
            this.subdirs = entries.filter { it.isDirectory }

            val subfiles = entries.filter { it.isFile }
            val startupSubfiles = mutableListOf<File>()
            val regularSubfiles = mutableListOf<File>()
            for (file in subfiles)
            {
                if (StartupFileNames.contains(file.name))
                    startupSubfiles.add(file)
                else
                    regularSubfiles.add(file)
            }

            this.startupSubfiles = startupSubfiles
            this.regularSubfiles = regularSubfiles
        }
    }
}

val DirectoryPickerUIState.HasPermission.isCurrentDirEmpty: Boolean
    get() = startupSubfiles.isEmpty() && subdirs.isEmpty() && regularSubfiles.isEmpty()