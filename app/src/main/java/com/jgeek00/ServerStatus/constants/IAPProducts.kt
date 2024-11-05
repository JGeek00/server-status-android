package com.jgeek00.ServerStatus.constants

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.QueryProductDetailsParams

val iapProducts = listOf(
    QueryProductDetailsParams.Product.newBuilder()
        .setProductId("com.jgeek00.server_status.small_tip")
        .setProductType(BillingClient.ProductType.INAPP)
        .build(),
    QueryProductDetailsParams.Product.newBuilder()
        .setProductId("com.jgeek00.server_status.medium_tip")
        .setProductType(BillingClient.ProductType.INAPP)
        .build(),
    QueryProductDetailsParams.Product.newBuilder()
        .setProductId("com.jgeek00.server_status.big_tip")
        .setProductType(BillingClient.ProductType.INAPP)
        .build(),
    QueryProductDetailsParams.Product.newBuilder()
        .setProductId("com.jgeek00.server_status.very_big_tip")
        .setProductType(BillingClient.ProductType.INAPP)
        .build(),
    QueryProductDetailsParams.Product.newBuilder()
        .setProductId("com.jgeek00.server_status.glorious_tip")
        .setProductType(BillingClient.ProductType.INAPP)
        .build()
)