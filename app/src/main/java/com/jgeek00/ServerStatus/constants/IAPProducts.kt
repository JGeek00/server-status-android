package com.jgeek00.ServerStatus.constants

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.QueryProductDetailsParams

val iapProducts = listOf(
    QueryProductDetailsParams.Product.newBuilder()
        .setProductId("com.jgeek00.ServerStatus.small_tip")
        .setProductType(BillingClient.ProductType.INAPP)
        .build(),
    QueryProductDetailsParams.Product.newBuilder()
        .setProductId("com.jgeek00.ServerStatus.medium_tip")
        .setProductType(BillingClient.ProductType.INAPP)
        .build(),
    QueryProductDetailsParams.Product.newBuilder()
        .setProductId("com.jgeek00.ServerStatus.big_tip")
        .setProductType(BillingClient.ProductType.INAPP)
        .build(),
    QueryProductDetailsParams.Product.newBuilder()
        .setProductId("com.jgeek00.ServerStatus.very_big_tip")
        .setProductType(BillingClient.ProductType.INAPP)
        .build(),
    QueryProductDetailsParams.Product.newBuilder()
        .setProductId("com.jgeek00.ServerStatus.glorious_tip")
        .setProductType(BillingClient.ProductType.INAPP)
        .build()
)