package com.tored.bridgelauncher.utils

import android.os.Build
import android.os.FileObserver
import java.io.File

// this class only exists because they deprecated a constructor on an abstract class which means I need two separate derived objects for different android versions
data class LambdaFileObserver(val file: File, val mask: Int, val onEvent: (event: Int, path: String?) -> Unit)
{
    val observer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
    {
        object : FileObserver(file, mask)
        {
            override fun onEvent(event: Int, path: String?): Unit = onEvent(event, path)
        }
    }
    else
    {
        @Suppress("DEPRECATION")
        object : FileObserver(file.path, mask)
        {
            override fun onEvent(event: Int, path: String?): Unit = onEvent(event, path)
        }
    }
}
