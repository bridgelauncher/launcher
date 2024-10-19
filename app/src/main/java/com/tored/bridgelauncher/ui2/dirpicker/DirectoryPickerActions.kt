package com.tored.bridgelauncher.ui2.dirpicker

data class DirectoryPickerActions(
    val dismiss: () -> Unit,
    val navigateToDirectory: (name: DirectoryPickerDirectory) -> Unit,
    val selectCurrentDirectory: () -> Unit,
    val requestFilterOrCreateDirectoryTextChange: (newText: String) -> Unit,
    val createSubdirectory: () -> Unit,
)
{
    companion object
    {
        fun empty() = DirectoryPickerActions({}, {}, {}, {}, {})
    }
}