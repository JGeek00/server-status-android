package com.jgeek00.ServerStatus.utils

fun createServerAddress(method: String, ipDomain: String, port: Int?, path: String?): String {
    var address = ""
    address += "$method://"
    address += ipDomain
    if (port != null && port != 0) {
        address += ":$port"
    }
    if (path != null) {
        address += path
    }
    return address
}