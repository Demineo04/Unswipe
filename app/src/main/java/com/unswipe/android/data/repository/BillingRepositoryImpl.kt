// Location: app/src/main/java/com/unswipe/android/data/repository/BillingRepositoryImpl.kt

package com.unswipe.android.data.repository

import android.app.Activity
import android.app.Application
import android.util.Log
import com.android.billingclient.api.*
import com.google.firebase.auth.FirebaseAuth // Added for User ID
import com.google.firebase.firestore.FirebaseFirestore
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
import javax.inject.Singleton // Billing setup often happens once

@Singleton // Mark as Singleton if BillingClient connection should be managed once
class BillingRepositoryImpl @Inject constructor( // <-- Hilt knows how to make this
    private val application: Application,          // <-- Hilt provides Application context
    private val billingClient: BillingClient,      // <-- Hilt provides this from AppModule
    private val settingsRepository: SettingsRepository, // <-- Hilt provides SettingsRepositoryImpl via Binds
    private val firestore: FirebaseFirestore,    // <-- Hilt provides this from FirebaseModule
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher, // <-- Hilt provides Dispatcher
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher // <-- For potential UI callbacks
) : BillingRepository, PurchasesUpdatedListener, BillingClientStateListener { // <-- Implement necessary listeners

    // Scope for this repository
    private val repositoryScope = CoroutineScope(ioDispatcher + SupervisorJob())

    // Use MutableStateFlow to hold and observe premium status
    private val _premiumStatusFlow = MutableStateFlow<Boolean>(false)

    // Hardcode your subscription ID (replace with your actual ID from Play Console)
    private val PREMIUM_SUB_ID = "unswipe_premium_monthly" // Or whatever you name it

    init {
        Log.d("BillingRepo", "Initializing BillingRepositoryImpl")
        startBillingConnection()
    }

    private fun startBillingConnection() {
        if (!billingClient.isReady) {
            Log.d("BillingRepo", "Starting BillingClient connection.")
            billingClient.startConnection(this) // 'this' implements BillingClientStateListener
        } else {
            Log.d("BillingRepo", "BillingClient already connected.")
            // Query purchases if already connected maybe?
            repositoryScope.launch { queryPurchases() }
        }
    }

    // --- BillingClientStateListener Implementation ---

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        Log.d("BillingRepo", "Billing setup finished. Code: ${billingResult.responseCode}")
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            // Query purchases and product details once setup is successful
            repositoryScope.launch {
                queryPurchases()
                // Optionally query product details here or on demand
            }
        } else {
            Log.e("BillingRepo", "Billing setup failed: ${billingResult.debugMessage}")
            // Handle setup failure - maybe retry later?
        }
    }

    override fun onBillingServiceDisconnected() {
        Log.w("BillingRepo", "Billing service disconnected. Trying to reconnect.")
        // Implement retry logic if desired, e.g., exponential backoff
        startBillingConnection() // Simple retry attempt
    }

    // --- PurchasesUpdatedListener Implementation ---

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        Log.d("BillingRepo", "onPurchasesUpdated. Code: ${billingResult.responseCode}, Purchases: ${purchases?.size}")
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                // Process valid purchases, typically requires acknowledgement
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
                    // Grant entitlement and acknowledge
                    handlePurchase(purchase)
                } else if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && purchase.isAcknowledged){
                    // Already acknowledged, just ensure premium status is correct
                    updatePremiumStatusFromPurchase(purchase)
                }
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.i("BillingRepo", "Purchase flow cancelled by user.")
            // Handle cancellation (e.g., show a message) - optional
        } else {
            Log.e("BillingRepo", "Purchase update error: ${billingResult.debugMessage}")
            // Handle other errors (e.g., item already owned, network error)
        }
    }

    // --- Core Billing Logic ---

    private fun handlePurchase(purchase: Purchase) {
        repositoryScope.launch {
            // CRITICAL FOR PRODUCTION: Verify purchase on your backend FIRST!
            // Send purchase.purchaseToken to your server, verify with Google Play API,
            // then update your canonical source of truth (e.g., Firestore) from the server.

            // For now, proceed with client-side acknowledgement & update (less secure)

            // Acknowledge the purchase (required within 3 days)
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                val ackResult = billingClient.acknowledgePurchase(acknowledgePurchaseParams) // suspending call
                Log.d("BillingRepo", "Acknowledge result for ${purchase.orderId}: ${ackResult.responseCode} ${ackResult.debugMessage}")
                if (ackResult.responseCode != BillingClient.BillingResponseCode.OK) {
                    Log.e("BillingRepo", "Failed to acknowledge purchase: ${purchase.purchaseToken}")
                    // Handle acknowledgement failure if needed
                }
            }
            // Update status based on the acknowledged purchase
            updatePremiumStatusFromPurchase(purchase)
        }
    }

    private suspend fun queryPurchases() {
        Log.d("BillingRepo", "Querying purchases...")
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS) // Query subscriptions
            .build()
        try {
            // Use queryPurchasesAsync (non-suspending, uses listener) or manage suspending calls carefully
            val purchasesResult = billingClient.queryPurchasesAsync(params) // Returns PurchasesResult via listener pattern is tricky here, let's simulate flow update
            // A better approach might use a separate flow updated by listeners.
            // For simplicity here, let's assume we refetch/update flow after operations.
            // Placeholder: Assume queryPurchasesAsync updates some internal state that _premiumStatusFlow reacts to.
            // In a real app, you'd structure this more robustly, maybe using callbackFlow.

            // Let's simulate checking active subs directly for now (less ideal than listener approach)
            val subsResult = billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build())
            var currentlyPremium = false
            subsResult.purchasesList.forEach { purchase ->
                if(purchase.products.contains(PREMIUM_SUB_ID) && purchase.purchaseState == Purchase.PurchaseState.PURCHASED){
                    currentlyPremium = true
                    if (!purchase.isAcknowledged) {
                        handlePurchase(purchase) // Acknowledge if needed
                    }
                }
            }
            if (_premiumStatusFlow.value != currentlyPremium) {
                Log.d("BillingRepo", "Updating premium status from query: $currentlyPremium")
                _premiumStatusFlow.value = currentlyPremium
                settingsRepository.setPremiumStatus(currentlyPremium)
                // Update firestore if needed (use with caution)
                // updateUserPremiumStatusInFirestore(currentlyPremium)
            }


        } catch (e: Exception) {
            Log.e("BillingRepo", "Error querying purchases", e)
        }
    }

    private suspend fun updatePremiumStatusFromPurchase(purchase: Purchase) {
        if (purchase.products.contains(PREMIUM_SUB_ID) && purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!_premiumStatusFlow.value) {
                Log.d("BillingRepo", "Granting premium status.")
                _premiumStatusFlow.value = true
                settingsRepository.setPremiumStatus(true)
                // updateUserPremiumStatusInFirestore(true) // Use with caution
            }
        } else {
            // Handle cases where purchase is for wrong product or state is not PURCHASED (e.g. PENDING)
            // Potentially revoke premium if a subscription expires and queryPurchases confirms this.
            // queryPurchases() logic should handle setting back to false if no active sub found.
        }
    }

    // --- Implement BillingRepository interface methods ---

    override fun getPremiumStatusFlow(): Flow<Boolean> {
        // Refresh status when collected?
        repositoryScope.launch { queryPurchases() }
        return _premiumStatusFlow.asStateFlow() // Expose the state flow
    }

    override suspend fun getSubscriptionProductDetails(productId: String): ProductDetails? = withContext(ioDispatcher) {
        if (!billingClient.isReady) {
            Log.e("BillingRepo", "Billing client not ready trying to get product details")
            return@withContext null
        }
        Log.d("BillingRepo", "Querying product details for $productId")
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.SUBS) // SUBS for subscription
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder().setProductList(productList).build()
        try {
            val result = billingClient.queryProductDetails(params) // Use suspending version
            Log.d("BillingRepo", "Product details query result: ${result.billingResult.responseCode}, List size: ${result.productDetailsList?.size}")
            result.productDetailsList?.firstOrNull { it.productId == productId }
        } catch (e: Exception){
            Log.e("BillingRepo", "Error querying product details", e)
            null
        }
    }

    // Note: Requires Activity context passed from the ViewModel/UI layer
    override fun launchPurchaseFlow(activity: Activity, productDetails: ProductDetails) {
        if (!billingClient.isReady) {
            Log.e("BillingRepo", "Billing client not ready trying to launch purchase flow")
            // Maybe show an error to the user via a callback or Flow
            return
        }
        // Find the correct offer token (e.g., base plan, could be more complex)
        val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken
        if (offerToken == null) {
            Log.e("BillingRepo", "No valid offer token found for ${productDetails.productId}")
            return
        }

        Log.d("BillingRepo", "Launching purchase flow for ${productDetails.productId} with offer token.")
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken) // Set the chosen offer token
                .build()
        )
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            // Optional: Obfuscated account/profile ID for fraud prevention
            // .setObfuscatedAccountId(...)
            // .setObfuscatedProfileId(...)
            .build()

        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        Log.d("BillingRepo", "Launch billing flow result code: ${billingResult.responseCode}")
        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            Log.e("BillingRepo", "Failed to launch billing flow: ${billingResult.debugMessage}")
            // Handle error (e.g., show message to user)
        }
    }

    // Re-check subscription status, e.g., on app resume
    override suspend fun checkSubscriptions() {
        if (billingClient.isReady) {
            queryPurchases()
        } else {
            Log.w("BillingRepo", "checkSubscriptions called but billing client not ready.")
            // Optionally try to reconnect
            // startBillingConnection()
        }
    }


    // Optional: Helper to update Firestore (use with caution, prefer backend)
    private fun updateUserPremiumStatusInFirestore(isPremium: Boolean) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return // Need auth instance
        Log.d("BillingRepo", "Attempting to update Firestore premium status for $userId to $isPremium")
        firestore.collection("users").document(userId)
            .set(mapOf("isPremium" to isPremium), com.google.firebase.firestore.SetOptions.merge()) // Use merge to avoid overwriting other fields
            .addOnSuccessListener { Log.d("BillingRepo", "Firestore premium status updated successfully.") }
            .addOnFailureListener { e -> Log.e("BillingRepo", "Error updating Firestore premium status", e) }
    }
}