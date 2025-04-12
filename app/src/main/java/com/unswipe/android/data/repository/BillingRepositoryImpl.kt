// Location: app/src/main/java/com/unswipe/android/data/repository/BillingRepositoryImpl.kt

package com.unswipe.android.data.repository

import android.app.Activity
import android.app.Application
import android.util.Log
import com.android.billingclient.api.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.unswipe.android.di.IoDispatcher
import com.unswipe.android.di.MainDispatcher
import com.unswipe.android.domain.repository.BillingRepository
import com.unswipe.android.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingRepositoryImpl @Inject constructor(
    private val application: Application,
    private val billingClient: BillingClient,
    private val settingsRepository: SettingsRepository,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth, // Added dependency
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : BillingRepository, PurchasesUpdatedListener, BillingClientStateListener {

    private val repositoryScope = CoroutineScope(ioDispatcher + SupervisorJob())
    private val _premiumStatusFlow = MutableStateFlow<Boolean>(false)

    // IMPORTANT: Replace with your actual Subscription ID from Google Play Console
    private val PREMIUM_SUB_ID = "unswipe_premium_monthly"

    companion object {
        private const val TAG = "BillingRepo"
    }

    init {
        Log.d(TAG, "Initializing BillingRepositoryImpl")
        startBillingConnection()
    }

    private fun startBillingConnection() {
        if (!billingClient.isReady) {
            Log.d(TAG, "Starting BillingClient connection.")
            billingClient.startConnection(this)
        } else {
            Log.d(TAG, "BillingClient already connected.")
            repositoryScope.launch { queryPurchases() }
        }
    }

    // --- BillingClientStateListener Implementation ---
    override fun onBillingSetupFinished(billingResult: BillingResult) {
        Log.d(TAG, "Billing setup finished. Code: ${billingResult.responseCode}")
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            repositoryScope.launch {
                queryPurchases()
            }
        } else {
            Log.e(TAG, "Billing setup failed: ${billingResult.debugMessage}")
        }
    }

    override fun onBillingServiceDisconnected() {
        Log.w(TAG, "Billing service disconnected. Trying to reconnect.")
        startBillingConnection() // Simple retry
    }

    // --- PurchasesUpdatedListener Implementation ---
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        Log.d(TAG, "onPurchasesUpdated. Code: ${billingResult.responseCode}, Purchases: ${purchases?.size}")
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                // Launch a coroutine to handle potentially suspending work
                repositoryScope.launch { // <-- FIX: Launch coroutine here
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        if (!purchase.isAcknowledged) {
                            handlePurchase(purchase) // Call suspend fun
                        } else {
                            // Already acknowledged, just ensure status is up-to-date
                            updatePremiumStatusFromPurchase(purchase) // Call suspend fun
                        }
                    } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                        Log.d(TAG,"Purchase is pending: ${purchase.purchaseToken}")
                        // Optionally handle pending state (e.g., show message)
                    }
                    // Handle other states if necessary
                }
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.i(TAG, "Purchase flow cancelled by user.")
        } else {
            Log.e(TAG, "Purchase update error: ${billingResult.debugMessage}")
        }
    }

    // --- Core Billing Logic ---
    private suspend fun handlePurchase(purchase: Purchase) { // Marked suspend
        Log.d(TAG,"Handling purchase: ${purchase.purchaseToken}")
        // TODO: CRITICAL FOR PRODUCTION: Implement backend verification FIRST!
        // Send purchase.purchaseToken to your server for verification before acknowledging/granting.

        // Acknowledge if needed
        if (!purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            // billingClient.acknowledgePurchase is a suspend function
            val ackResult = billingClient.acknowledgePurchase(acknowledgePurchaseParams)
            Log.d(TAG, "Acknowledge result for ${purchase.orderId}: ${ackResult.responseCode} ${ackResult.debugMessage}")
            if (ackResult.responseCode != BillingClient.BillingResponseCode.OK) {
                Log.e(TAG, "Failed to acknowledge purchase: ${purchase.purchaseToken}")
            }
        }
        // Update status after potential acknowledgement
        updatePremiumStatusFromPurchase(purchase)
    }

    private suspend fun queryPurchases() { // Marked suspend
        Log.d(TAG, "Querying purchases...")
        if (!billingClient.isReady) {
            Log.w(TAG, "Querying purchases called but BillingClient not ready.")
            return // Or try reconnecting?
        }
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        try {
            // Use suspend version directly
            val purchasesResult = billingClient.queryPurchasesAsync(params)
            var currentlyPremium = false
            Log.d(TAG, "Query purchases result code: ${purchasesResult.billingResult.responseCode}, List size: ${purchasesResult.purchasesList.size}")

            purchasesResult.purchasesList.forEach { purchase ->
                if (purchase.products.contains(PREMIUM_SUB_ID) && purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    currentlyPremium = true
                    // Acknowledge if needed during query (sometimes necessary if missed)
                    if (!purchase.isAcknowledged) {
                        Log.w(TAG, "Found unacknowledged purchase during query, handling: ${purchase.purchaseToken}")
                        handlePurchase(purchase)
                    }
                }
            }

            // Update flow and local settings if status changed
            if (_premiumStatusFlow.value != currentlyPremium) {
                Log.d(TAG, "Updating premium status from query: $currentlyPremium")
                _premiumStatusFlow.value = currentlyPremium
                settingsRepository.setPremiumStatus(currentlyPremium) // Call suspend fun
                // Update firestore cautiously if needed (prefer backend)
                // updateUserPremiumStatusInFirestore(currentlyPremium)
            } else {
                Log.d(TAG, "Premium status from query hasn't changed ($currentlyPremium)")
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error querying purchases", e)
        }
    }

    // Needs to be suspend because it calls settingsRepository.setPremiumStatus (which is suspend)
    private suspend fun updatePremiumStatusFromPurchase(purchase: Purchase) { // Marked suspend
        val isTargetProduct = purchase.products.contains(PREMIUM_SUB_ID)
        val isPurchased = purchase.purchaseState == Purchase.PurchaseState.PURCHASED

        if (isTargetProduct && isPurchased) {
            if (!_premiumStatusFlow.value) {
                Log.d(TAG, "Granting premium status for product: ${purchase.products.firstOrNull()}")
                _premiumStatusFlow.value = true
                settingsRepository.setPremiumStatus(true)
                // updateUserPremiumStatusInFirestore(true)
            }
        } else {
            // This function might not be the place to revoke premium.
            // queryPurchases() should handle setting status to false if no active sub is found.
            Log.d(TAG,"updatePremiumStatusFromPurchase called for non-target or non-purchased state: ${purchase.purchaseState} for products ${purchase.products}")
        }
    }

    // --- Implement BillingRepository interface methods ---
    override fun getPremiumStatusFlow(): Flow<Boolean> {
        repositoryScope.launch { queryPurchases() } // Refresh on collect
        return _premiumStatusFlow.asStateFlow()
    }

    override suspend fun getSubscriptionProductDetails(productId: String): ProductDetails? = withContext(ioDispatcher) {
        if (!billingClient.isReady) return@withContext null
        Log.d(TAG, "Querying product details for $productId")
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder().setProductList(productList).build()
        try {
            val result = billingClient.queryProductDetails(params) // Suspend version
            result.productDetailsList?.firstOrNull { it.productId == productId }
        } catch (e: Exception){
            Log.e(TAG, "Error querying product details", e)
            null
        }
    }

    override fun launchPurchaseFlow(activity: Activity, productDetails: ProductDetails) {
        if (!billingClient.isReady) {
            Log.e(TAG, "Billing client not ready for purchase flow")
            return
        }
        val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken
        if (offerToken == null) {
            Log.e(TAG, "No valid offer token found for ${productDetails.productId}")
            return
        }
        Log.d(TAG, "Launching purchase flow for ${productDetails.productId}")
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()
        )
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            Log.e(TAG, "Failed to launch billing flow: ${billingResult.debugMessage}")
        }
    }

    override suspend fun checkSubscriptions() {
        if (billingClient.isReady) {
            queryPurchases()
        } else {
            Log.w(TAG, "checkSubscriptions called but billing client not ready.")
        }
    }

    // Optional: Helper to update Firestore (use with caution, prefer backend)
    private fun updateUserPremiumStatusInFirestore(isPremium: Boolean) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        Log.d(TAG, "Attempting to update Firestore premium status for $userId to $isPremium")
        firestore.collection("users").document(userId)
            .set(mapOf("isPremium" to isPremium), SetOptions.merge())
            .addOnSuccessListener { Log.d(TAG, "Firestore premium status updated successfully.") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating Firestore premium status", e) }
    }
}