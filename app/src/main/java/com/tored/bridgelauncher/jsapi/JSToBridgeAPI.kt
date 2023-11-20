package com.tored.bridgelauncher.jsapi

import android.content.Context
import android.webkit.JavascriptInterface
import android.widget.Toast

class JSToBridgeAPI(
    private val _context: Context,
) : Any()
{
    @JavascriptInterface
    fun showToast(message: String, long: Boolean = false)
    {
        Toast.makeText(_context, message, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
    }
}