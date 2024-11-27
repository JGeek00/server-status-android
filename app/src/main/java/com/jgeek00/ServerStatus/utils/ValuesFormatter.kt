package com.jgeek00.ServerStatus.utils

import java.util.Locale

fun formatMemory(value: Long?): String {
    val v = value ?: return "N/A"
    val calculated = v.toDouble()/1048576.0
    return String.format(Locale.getDefault(), "%.2f", calculated)
}

fun formatStorage(value: Double?): String {
    val v = value ?: return "N/A"
    return if (v / 1000000000 > 1000) {
        val calculated = v / 1000000000000
        "${String.format(Locale.getDefault(), "%.1f", calculated)} TB"
    } else {
        val calculated = v / 1000000000
        "${String.format(Locale.getDefault(), "%.1f", calculated)} GB"
    }
}

fun formatBits(value: Long?): String {
    val v = value ?: return "0 Bit/s"
    val kbps = v / 1000.0
    return when {
        kbps <= 1000 -> "${String.format(Locale.getDefault(), "%.1f", kbps)} Kbit/s"
        kbps / 1000.0 <= 1000 -> "${String.format(Locale.getDefault(), "%.1f", kbps / 1000.0)} Mbit/s"
        kbps / 1000000.0 <= 1000 -> "${String.format(Locale.getDefault(), "%.1f", kbps / 1000000.0)} Gbit/s"
        else -> "N/A"
    }
}

fun formatBytes(value: Long?): String {
    val v = value ?: return "0 B/s"
    val kbps = v / 8.0 / 1000.0
    return when {
        kbps <= 1000 -> "${String.format(Locale.getDefault(), "%.1f", kbps)} KB/s"
        kbps / 1000.0 <= 1000 -> "${String.format(Locale.getDefault(), "%.1f", kbps / 1000.0)} MB/s"
        kbps / 1_000_000.0 <= 1000 -> "${String.format(Locale.getDefault(), "%.1f", kbps / 1000000.0)} GB/s"
        else -> "N/A"
    }
}

fun cacheValue(value: Long?): String {
    val v = value ?: return "N/A"
    val calculated = v.toDouble()/1000.0
    return "${String.format(Locale.getDefault(), "%.2f", calculated)} MB"
}
