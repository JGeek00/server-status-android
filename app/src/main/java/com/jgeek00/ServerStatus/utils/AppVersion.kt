package com.jgeek00.ServerStatus.utils

import android.content.Context
import android.content.pm.PackageManager

fun getAppVersion(context: Context): String? {
    try {
        val versionName = context.packageManager.getPackageInfo(context.getPackageName(), 0).versionName
        return versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        return null
    }
}