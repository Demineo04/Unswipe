package com.unswipe.android.domain.repository

import android.app.Activity
import com.android.billingclient.api.ProductDetails // Import from billing library
import kotlinx.coroutines.flow.Flow

interface BillingRepository {

    /**
     * Provides a Flow indicating the user's current premium subscription status (true if active, false otherwise).
     * This should be the authoritative source for premium status.
     */
    fun getPremiumStatusFlow(): Flow<Boolean>

    /**
     * Fetches the product details for a given subscription ID from Google Play.
     * Returns null if not found or an error occurs.
     */
    suspend fun getSubscriptionProductDetails(productId: String): ProductDetails?

    /**
     * Initiates the Google Play purchase flow for the given product details.
     * Requires the current Activity to launch the billing UI.
     */
    fun launchPurchaseFlow(activity: Activity, productDetails: ProductDetails)

    /**
     * Manually triggers a check/refresh of the user's current subscriptions with Google Play.
     * Useful to call on app resume or specific events.
     */
    suspend fun checkSubscriptions()

}