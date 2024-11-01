package com.jgeek00.ServerStatus.utils

import java.util.Base64

fun encodeCredentialsBasicAuth(username: String, password: String): String? {
    val combinedString = "$username:$password"

    val encodedString = combinedString.toByteArray(Charsets.UTF_8)
        .toString(Charsets.UTF_8)
        .replace(" ", "%20")

    return Base64.getEncoder().encodeToString(encodedString.toByteArray())
}
