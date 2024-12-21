package com.tored.bridgelauncher.ui2.dirpicker

import com.tored.bridgelauncher.api2.server.files.BridgeFileServer
import com.tored.bridgelauncher.utils.CurrentAndroidVersion
import java.io.File
import kotlin.test.assertTrue

enum class DirectoryPickerMode
{
    LoadProject,
    MockExport,
}

sealed class DirectoryPickerState
{
    abstract val mode: DirectoryPickerMode

    data class NoStoragePermission(
        override val mode: DirectoryPickerMode,
        val supportsScopedStorage: Boolean = CurrentAndroidVersion.supportsScopedStorage(),
    ) : DirectoryPickerState()

    data class HasStoragePermission(
        override val mode: DirectoryPickerMode,
        val currentDirectory: DirectoryPickerDirectory,
        val upDirectory: DirectoryPickerDirectory?,
        val directories: List<DirectoryPickerDirectory>,
        val startupFiles: List<DirectoryPickerFile>,
        val regularFiles: List<DirectoryPickerFile>,
        val filterOrCreateDirectoryText: String,
    ) : DirectoryPickerState()
    {
        fun refresh(): HasStoragePermission
        {
            assertTrue(currentDirectory is DirectoryPickerRealDirectory)
            return fromDirectoryAndFilter(mode, currentDirectory.file, filterOrCreateDirectoryText)
        }

        fun navigateTo(directory: File, filterOrCreateDirectoryText: String = ""): HasStoragePermission
        {
            return fromDirectoryAndFilter(mode, directory, filterOrCreateDirectoryText)
        }

        companion object
        {
            fun fromDirectoryAndFilter(
                mode: DirectoryPickerMode,
                directory: File,
                filterOrCreateDirectoryText: String = "",
            ): HasStoragePermission
            {
                val filteredFilesAndDirectories = (
                        if (filterOrCreateDirectoryText.isBlank())
                            directory.listFiles()
                        else
                            directory.listFiles { _, filename -> filename.contains(filterOrCreateDirectoryText, ignoreCase = true) }
                    )?.sortedBy { it.name }
                    ?: listOf()

                val filesGroupedByIsStartup = filteredFilesAndDirectories
                    .filter{ it.isFile }
                    .groupBy { BridgeFileServer.StartupFileNames.contains(it.name) }

                return HasStoragePermission(
                    mode = mode,
                    currentDirectory = directory.toDirectoryPickerDirectory(),
                    upDirectory = directory.parentFile?.toDirectoryPickerDirectory(),
                    directories = filteredFilesAndDirectories.filter { it.isDirectory }.map { it.toDirectoryPickerDirectory() },
                    startupFiles = filesGroupedByIsStartup[true]?.map { it.toDirectoryPickerFile() } ?: listOf(),
                    regularFiles = filesGroupedByIsStartup[false]?.map { it.toDirectoryPickerFile() } ?: listOf(),
                    filterOrCreateDirectoryText = filterOrCreateDirectoryText,
                )
            }
        }
    }
}

interface DirectoryPickerDirectory
{
    val path: String
    val name: String
    val canRead: Boolean
}

data class DirectoryPickerRealDirectory(val file: File) : DirectoryPickerDirectory
{
    override val name: String = file.name
    override val path: String = file.path
    override val canRead: Boolean = file.canRead()
}

fun File.toDirectoryPickerDirectory() = DirectoryPickerRealDirectory(this)

data class DirectoryPickerDummyDirectory(
    override val name: String,
    override val path: String,
    override val canRead: Boolean = true,
) : DirectoryPickerDirectory


interface DirectoryPickerFile
{
    val name: String
    val path: String
    val canRead: Boolean
}

data class DirectoryPickerRealFile(val file: File) : DirectoryPickerFile
{
    override val name: String = file.name
    override val path: String = file.path
    override val canRead: Boolean = file.canRead()

}

fun File.toDirectoryPickerFile() = DirectoryPickerRealFile(this)

data class DirectoryPickerDummyFile(
    override val name: String,
    override val path: String,
    override val canRead: Boolean = true,
) : DirectoryPickerFile
