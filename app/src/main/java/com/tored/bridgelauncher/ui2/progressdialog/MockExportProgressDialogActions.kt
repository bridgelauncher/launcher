package com.tored.bridgelauncher.ui2.progressdialog

data class MockExportProgressDialogActions(
    val dismiss: () -> Unit,
)
{
    companion object
    {
        fun empty() = MockExportProgressDialogActions({})
    }
}