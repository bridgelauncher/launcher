package com.tored.bridgelauncher.utils

import android.os.Build

class CurrentAndroidVersion
{
    companion object
    {
        private fun from(versionCode: Int) = Build.VERSION.SDK_INT >= versionCode

        fun supportsScopedStorage() = from(Build.VERSION_CODES.R)
        fun supportsNightMode() = from(Build.VERSION_CODES.R)
        fun supportsNightModeCustom() = from(Build.VERSION_CODES.R)
        fun supportsNavBarContrastEnforcement() = from(Build.VERSION_CODES.Q)
        fun supportsQSTileSubtitle() = from(Build.VERSION_CODES.Q)
        fun supportsQSTilePrompt() = from(Build.VERSION_CODES.TIRAMISU)
        fun supportsWebViewSafeBrowsing() = from(Build.VERSION_CODES.O)
        fun supportsDisplayShape() = from(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        fun supportsDisplayCutout() = from(Build.VERSION_CODES.S)
        fun supportsAccessiblityServiceScreenLock() = from(Build.VERSION_CODES.P)
        fun supportsPackageInfoLongVersionCode() = from(Build.VERSION_CODES.P)
    }
}