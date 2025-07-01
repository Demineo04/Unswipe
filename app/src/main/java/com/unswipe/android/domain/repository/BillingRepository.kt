package com.unswipe.android.domain.repository

import android.app.Activity
import com.unswipe.android.domain.model.*
import kotlinx.coroutines.flow.Flow

interface BillingRepository {
    
    /**
     * Initialize billing client and connect to Google Play
     */
    suspend fun initializeBilling(): BillingResult
    
    /**
     * Get available subscription products from Google Play
     */
    suspend fun getAvailableProducts(): Result<List<BillingProduct>>
    
    /**
     * Launch purchase flow for a specific product
     */
    suspend fun launchPurchaseFlow(
        activity: Activity,
        productId: String,
        isUpgrade: Boolean = false,
        oldProductId: String? = null
    ): BillingResult
    
    /**
     * Get current active purchases/subscriptions
     */
    suspend fun getActivePurchases(): Result<List<Purchase>>
    
    /**
     * Acknowledge a purchase (required for subscriptions)
     */
    suspend fun acknowledgePurchase(purchaseToken: String): BillingResult
    
    /**
     * Get subscription status for active subscriptions
     */
    suspend fun getSubscriptionStatus(): Result<List<SubscriptionStatus>>
    
    /**
     * Check if user has any active premium subscription
     */
    suspend fun hasActivePremiumSubscription(): Boolean
    
    /**
     * Get the current premium tier based on active subscriptions
     */
    suspend fun getCurrentPremiumTier(): PremiumTier
    
    /**
     * Flow that emits billing connection state changes
     */
    fun getBillingConnectionState(): Flow<Boolean>
    
    /**
     * Flow that emits purchase updates
     */
    fun getPurchaseUpdates(): Flow<List<Purchase>>
    
    /**
     * Handle pending purchases (for slow payment methods)
     */
    suspend fun handlePendingPurchases(): BillingResult
    
    /**
     * Restore purchases (useful for account recovery)
     */
    suspend fun restorePurchases(): BillingResult
    
    /**
     * Get price information for a specific product
     */
    suspend fun getProductPrice(productId: String): Result<PriceInfo>
    
    /**
     * Check if billing is supported on this device
     */
    fun isBillingSupported(): Boolean
    
    /**
     * Get available payment methods (limited info from Google Play)
     */
    suspend fun getAvailablePaymentMethods(): Result<List<PaymentMethod>>
    
    /**
     * Cancel subscription (redirects to Google Play subscription management)
     */
    fun cancelSubscription(productId: String)
    
    /**
     * Manage subscription (redirects to Google Play subscription management)
     */
    fun manageSubscription(productId: String)
    
    /**
     * Get subscription renewal date
     */
    suspend fun getSubscriptionRenewalDate(productId: String): Result<Long>
    
    /**
     * Check if subscription is in grace period
     */
    suspend fun isSubscriptionInGracePeriod(productId: String): Boolean
    
    /**
     * Get promotional offers available for the user
     */
    suspend fun getAvailableOffers(productId: String): Result<List<SubscriptionOffer>>
    
    /**
     * Validate purchase on our backend (if needed)
     */
    suspend fun validatePurchase(purchase: Purchase): Result<Boolean>
    
    /**
     * Get purchase history for analytics
     */
    suspend fun getPurchaseHistory(): Result<List<Purchase>>
    
    /**
     * End billing connection
     */
    fun endConnection()
}