package com.jgeek00.ServerStatus.navigation

class Routes {
    companion object {
        const val ROUTE_STATUS = "status"

        const val ROUTE_SETTINGS = "settings"

        const val ROUTE_SERVER_FORM = "server-form?{serverId}"
        const val ARG_SERVER_ID = "serverId"

        const val ONBOARDING = "onboarding"

        const val ROUTE_CPU_DETAILS = "cpu-details"
        const val ROUTE_MEMORY_DETAILS = "memory-details"
        const val ROUTE_STORAGE_DETAILS = "storage-details"
        const val ROUTE_NETWORK_DETAILS = "network-details"

        const val ROUTE_EMPTY = "route-empty"
    }
}