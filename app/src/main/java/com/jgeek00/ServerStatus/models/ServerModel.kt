package com.jgeek00.ServerStatus.models

data class ServerModel(
    val id: Int,
    val name: String,
    val method: String,
    val ipDomain: String,
    val port: Int?,
    val path: String?,
    val useBasicAuth: Boolean,
    val basicAuthUser: String?,
    val basicAuthPassword: String?,
)
