package com.tored.bridgelauncher.ui.dirpicker

import java.io.File

// renaming this because it's stupid that directories are also handled by File
typealias Directory = File

val StartupFileNames = setOf(
    "index.html"
)

sealed interface DirPickerUIState
{
    object NoPermission : DirPickerUIState

    data class HasPermission(
        val currentDir: Directory,
        val filterOrCreateDirText: String,
        val exportState: DirPickerExportState?,
    ) : DirPickerUIState
    {
        var isDirEmpty: Boolean
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

            val filterPhrase = filterOrCreateDirText.lowercase()
            var anyFiles = false
            val files = currentDir.listFiles { _, filename ->
                filename.lowercase().contains(filterPhrase).also {
                    anyFiles = true
                }
            }

            val entries = files?.sortedBy { it.name } ?: emptyList()

            this.isDirEmpty = !anyFiles
            this.subdirs = entries.filter { it.isDirectory }

            val subfiles = entries.filter { it.isFile }

            if (exportState != null)
            {
                this.startupSubfiles = emptyList()
                this.regularSubfiles = subfiles
            }
            else
            {
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
}

val DirPickerUIState.HasPermission.hasNothingtoShow: Boolean
    get() = startupSubfiles.isEmpty() && subdirs.isEmpty() && regularSubfiles.isEmpty()

sealed interface DirPickerExportState
{
    object NotExporting : DirPickerExportState

    data class Exporting(var completedCount: Int = 0, var startedCount: Int = 0) : DirPickerExportState
}