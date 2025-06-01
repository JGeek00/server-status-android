package com.jgeek00.ServerStatus.models

data class StatusResult (
    val cpu: CPU? = null,
    val memory: Memory? = null,
    val storage: List<Storage>? = null,
    val network: Network? = null,
    val host: Host? = null
)

data class CPU (
    val model: String? = null,
    val utilisation: Double? = null,
    val count: Long? = null,
    val cache: Long? = null,
    val cores: Long? = null,
    val cpuCores: List<CPUCore>? = null
)

data class CPUCore (
    val temperatures: List<Double?>? = null,
    val frequencies: Frequencies? = null
)

data class Frequencies (
    val now: Long? = null,
    val min: Long? = null,
    val base: Long? = null,
    val max: Long? = null
)

data class Host (
    val uptime: Double? = null,
    val os: String? = null,
    val hostname: String? = null,
    val appMemory: String? = null,
    val loadavg: List<Double>? = null
)

data class Memory (
    val total: Long? = null,
    val available: Long? = null,
    val cached: Long? = null,
    val swap_total: Long? = null,
    val swap_available: Long? = null,
    val processes: Long? = null
)

data class Network (
    val networkInterface: String? = null,
    val speed: Long? = null,
    val rx: Long? = null,
    val tx: Long? = null
)

data class Storage (
    val name: String? = null,
    val icon: String? = null,
    val total: Double? = null,
    val available: Long? = null
)
