package com.jgeek00.ServerStatus.views.Settings

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.MoneyOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jgeek00.ServerStatus.R
import com.jgeek00.ServerStatus.components.ListTile
import com.jgeek00.ServerStatus.di.BillingRepositoryEntryPoint
import com.jgeek00.ServerStatus.navigation.NavigationManager
import dagger.hilt.android.EntryPointAccessors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipsView() {
    val context = LocalContext.current

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    fun findActivity(): Activity {
        while (context is ContextWrapper) {
            if (context is Activity) return context
        }
        throw IllegalStateException("no activity")
    }

    val billingRepository = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            BillingRepositoryEntryPoint::class.java
        ).billingRepository
    }

    val products = billingRepository.products.collectAsState(initial = emptyList()).value
    val sortedProducts = products.sortedBy { it.oneTimePurchaseOfferDetails?.priceAmountMicros }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            LargeTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                ),
                scrollBehavior = scrollBehavior,
                title = {
                    Text(stringResource(R.string.tips))
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            NavigationManager.getInstance().popBack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        val displayCutout = if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) WindowInsets.displayCutout else WindowInsets(0.dp)
        if (sortedProducts.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .windowInsetsPadding(displayCutout)
                    .padding(padding)
            ) {
                sortedProducts.map { product ->
                    ListTile(
                        label = product.name,
                        supportingText = product.description,
                        trailing = {
                            product.oneTimePurchaseOfferDetails?.formattedPrice?.let {
                                Box(
                                    contentAlignment = Alignment.CenterEnd,
                                    modifier = Modifier
                                        .width(100.dp)
                                ) {
                                    Text(
                                        text = "$it ${product.oneTimePurchaseOfferDetails?.priceCurrencyCode}",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp,
                                        textAlign = TextAlign.End
                                    )
                                }
                            }
                        },
                        onClick = {
                            if (product.oneTimePurchaseOfferDetails?.formattedPrice != null) {
                                billingRepository.launchPurchaseFlow(findActivity(), product)
                            }
                        }
                    )
                }
            }
        }
        else {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(padding)
                        .windowInsetsPadding(displayCutout)
                        .fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MoneyOff,
                        contentDescription = stringResource(R.string.error),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = stringResource(R.string.no_tip_options_available_right_now),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 22.sp
                    )
                }
            }
        }
        if (billingRepository.purchaseFailed.value) {
            AlertDialog(
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Error,
                        contentDescription = stringResource(R.string.error),
                    )
                },
                title = {
                    Text(
                        text = stringResource(R.string.purchase_failed),
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Text(text = stringResource(R.string.an_error_occurred_while_the_purchase_was_being_processed_or_it_has_been_cancelled_by_the_user))
                },
                onDismissRequest = {},
                confirmButton = {},
                dismissButton = {
                    TextButton(
                        onClick = { billingRepository.purchaseFailed.value = false }
                    ) {
                        Text(stringResource(R.string.close))
                    }
                }
            )
        }
        if (billingRepository.purchaseSuccessful.value) {
            AlertDialog(
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = stringResource(R.string.success),
                    )
                },
                title = {
                    Text(
                        text = stringResource(R.string.purchase_completed_successfully),
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Text(text = stringResource(R.string.the_purchase_was_completed_successfully_thank_you_for_your_support))
                },
                onDismissRequest = {},
                confirmButton = {},
                dismissButton = {
                    TextButton(
                        onClick = { billingRepository.purchaseFailed.value = false }
                    ) {
                        Text(stringResource(R.string.close))
                    }
                }
            )
        }
    }
}