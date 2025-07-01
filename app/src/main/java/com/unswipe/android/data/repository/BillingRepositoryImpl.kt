// Location: app/src/main/java/com/unswipe/android/data/repository/BillingRepositoryImpl.kt

package com.unswipe.android.data.repository

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.android.billingclient.api.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.unswipe.android.di.IoDispatcher
import com.unswipe.android.di.MainDispatcher
import com.unswipe.android.domain.model.*
import com.unswipe.android.domain.repository.BillingRepository
import com.unswipe.android.domain.repository.PremiumRepository
import com.unswipe.android.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.LocalDateTime
import kotlin.coroutines.resume

@Singleton
class BillingRepositoryImpl @Inject constructor(
    private val application: Application,
    private val billingClient: BillingClient,
    private val settingsRepository: SettingsRepository,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth, // Added dependency
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    private val context: Context,
    private val premiumRepository: PremiumRepository
) : BillingRepository, PurchasesUpdatedListener, BillingClientStateListener {

    private val repositoryScope = CoroutineScope(ioDispatcher + SupervisorJob())
    private val _premiumStatusFlow = MutableStateFlow<Boolean>(false)
    private var billingClient: BillingClient? = null
    
    private val _billingConnectionState = MutableStateFlow(false)
    private val _purchaseUpdates = MutableStateFlow<List<Purchase>>(emptyList())
    
    private val connectionState: StateFlow<Boolean> = _billingConnectionState.asStateFlow()
    private val purchaseUpdates: StateFlow<List<Purchase>> = _purchaseUpdates.asStateFlow()

    // IMPORTANT: Replace with your actual Subscription ID from Google Play Console
    private val PREMIUM_SUB_ID = "unswipe_premium_monthly"

    companion object {
        private const val TAG = "BillingRepo"
    }

    init {
        Log.d(TAG, "Initializing BillingRepositoryImpl")
        initializeBillingClient()
    }

    private fun initializeBillingClient() {
        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases()
            .build()
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

    override suspend fun initializeBilling(): BillingResult {
        return suspendCancellableCoroutine { continuation ->
            if (billingClient?.isReady == true) {
                continuation.resume(
                    BillingResult(
                        responseCode = BillingResponseCode.OK,
                        debugMessage = "Already connected"
                    )
                )
                return@suspendCancellableCoroutine
            }

            billingClient?.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: com.android.billingclient.api.BillingResult) {
                    val result = billingResult.toBillingResult()
                    _billingConnectionState.value = result.isSuccess
                    continuation.resume(result)
                }

                override fun onBillingServiceDisconnected() {
                    _billingConnectionState.value = false
                }
            })
        }
    }

    override suspend fun getAvailableProducts(): Result<List<BillingProduct>> {
        return try {
            if (billingClient?.isReady != true) {
                return Result.failure(Exception("Billing client not ready"))
            }

            val subscriptionParams = QueryProductDetailsParams.newBuilder()
                .setProductList(
                    BillingProducts.ALL_SUBSCRIPTION_PRODUCTS.map { productId ->
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(productId)
                            .setProductType(BillingClient.ProductType.SUBS)
                            .build()
                    }
                )
                .build()

            val oneTimeParams = QueryProductDetailsParams.newBuilder()
                .setProductList(
                    BillingProducts.ALL_ONE_TIME_PRODUCTS.map { productId ->
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(productId)
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build()
                    }
                )
                .build()

            val subscriptionResult = queryProductDetails(subscriptionParams)
            val oneTimeResult = queryProductDetails(oneTimeParams)

            val allProducts = mutableListOf<BillingProduct>()
            
            subscriptionResult.getOrNull()?.let { products ->
                allProducts.addAll(products.map { it.toBillingProduct(ProductType.SUBSCRIPTION) })
            }
            
            oneTimeResult.getOrNull()?.let { products ->
                allProducts.addAll(products.map { it.toBillingProduct(ProductType.ONE_TIME_PURCHASE) })
            }

            Result.success(allProducts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun launchPurchaseFlow(
        activity: Activity,
        productId: String,
        isUpgrade: Boolean,
        oldProductId: String?
    ): BillingResult {
        return try {
            if (billingClient?.isReady != true) {
                return BillingResult(
                    responseCode = BillingResponseCode.SERVICE_DISCONNECTED,
                    debugMessage = "Billing client not ready"
                )
            }

            val productDetails = getProductDetails(productId)
                ?: return BillingResult(
                    responseCode = BillingResponseCode.ITEM_UNAVAILABLE,
                    debugMessage = "Product not found: $productId"
                )

            val productDetailsParamsList = listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .build()
            )

            val billingFlowParamsBuilder = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)

            // Handle subscription upgrades/downgrades
            if (isUpgrade && oldProductId != null) {
                val oldPurchase = getActivePurchaseForProduct(oldProductId)
                oldPurchase?.let { purchase ->
                    billingFlowParamsBuilder.setSubscriptionUpdateParams(
                        BillingFlowParams.SubscriptionUpdateParams.newBuilder()
                            .setOldPurchaseToken(purchase.purchaseToken)
                            .setReplaceProrationMode(BillingFlowParams.ProrationMode.IMMEDIATE_WITH_TIME_PRORATION)
                            .build()
                    )
                }
            }

            val billingResult = billingClient!!.launchBillingFlow(
                activity,
                billingFlowParamsBuilder.build()
            )

            billingResult.toBillingResult()
        } catch (e: Exception) {
            BillingResult(
                responseCode = BillingResponseCode.ERROR,
                debugMessage = e.message ?: "Unknown error"
            )
        }
    }

    override suspend fun getActivePurchases(): Result<List<Purchase>> {
        return try {
            if (billingClient?.isReady != true) {
                return Result.failure(Exception("Billing client not ready"))
            }

            val subscriptionResult = billingClient!!.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            )

            val inAppResult = billingClient!!.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            )

            val allPurchases = mutableListOf<Purchase>()
            
            suspendCancellableCoroutine { continuation ->
                billingClient!!.queryPurchasesAsync(
                    QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                ) { billingResult, purchases ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        allPurchases.addAll(purchases.map { it.toPurchase() })
                    }
                    
                    billingClient!!.queryPurchasesAsync(
                        QueryPurchasesParams.newBuilder()
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build()
                    ) { inAppBillingResult, inAppPurchases ->
                        if (inAppBillingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            allPurchases.addAll(inAppPurchases.map { it.toPurchase() })
                        }
                        continuation.resume(Unit)
                    }
                }
            }

            Result.success(allPurchases)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun acknowledgePurchase(purchaseToken: String): BillingResult {
        return try {
            if (billingClient?.isReady != true) {
                return BillingResult(
                    responseCode = BillingResponseCode.SERVICE_DISCONNECTED,
                    debugMessage = "Billing client not ready"
                )
            }

            suspendCancellableCoroutine { continuation ->
                billingClient!!.acknowledgePurchase(
                    AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchaseToken)
                        .build()
                ) { billingResult ->
                    continuation.resume(billingResult.toBillingResult())
                }
            }
        } catch (e: Exception) {
            BillingResult(
                responseCode = BillingResponseCode.ERROR,
                debugMessage = e.message ?: "Unknown error"
            )
        }
    }

    override suspend fun getSubscriptionStatus(): Result<List<SubscriptionStatus>> {
        return try {
            val purchases = getActivePurchases().getOrNull() ?: return Result.success(emptyList())
            
            val subscriptionStatuses = purchases
                .filter { it.purchaseState == PurchaseState.PURCHASED }
                .map { purchase ->
                    SubscriptionStatus(
                        productId = purchase.productId,
                        isActive = true,
                        isExpired = false,
                        expiryTime = null, // Would need server-side validation for accurate expiry
                        autoRenewing = purchase.isAutoRenewing,
                        purchaseToken = purchase.purchaseToken,
                        orderId = purchase.orderId
                    )
                }
            
            Result.success(subscriptionStatuses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun hasActivePremiumSubscription(): Boolean {
        return try {
            val purchases = getActivePurchases().getOrNull() ?: return false
            purchases.any { purchase ->
                purchase.purchaseState == PurchaseState.PURCHASED &&
                BillingProducts.ALL_SUBSCRIPTION_PRODUCTS.contains(purchase.productId)
            }
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getCurrentPremiumTier(): PremiumTier {
        return try {
            val purchases = getActivePurchases().getOrNull() ?: return PremiumTier.FREE
            
            val activePremiumPurchase = purchases
                .filter { it.purchaseState == PurchaseState.PURCHASED }
                .find { BillingProducts.ALL_SUBSCRIPTION_PRODUCTS.contains(it.productId) || it.productId == BillingProducts.LIFETIME_PREMIUM }
            
            activePremiumPurchase?.let { purchase ->
                BillingProducts.getProductTier(purchase.productId)
            } ?: PremiumTier.FREE
        } catch (e: Exception) {
            PremiumTier.FREE
        }
    }

    override fun getBillingConnectionState(): Flow<Boolean> = connectionState

    override fun getPurchaseUpdates(): Flow<List<Purchase>> = purchaseUpdates

    override suspend fun handlePendingPurchases(): BillingResult {
        return try {
            val purchases = getActivePurchases().getOrNull() ?: return BillingResult(
                responseCode = BillingResponseCode.OK,
                debugMessage = "No purchases to handle"
            )
            
            purchases
                .filter { it.purchaseState == PurchaseState.PENDING }
                .forEach { purchase ->
                    // Handle pending purchase - typically just wait for Google Play to process
                    // You might want to show a pending state to the user
                }
            
            BillingResult(
                responseCode = BillingResponseCode.OK,
                debugMessage = "Pending purchases handled"
            )
        } catch (e: Exception) {
            BillingResult(
                responseCode = BillingResponseCode.ERROR,
                debugMessage = e.message ?: "Error handling pending purchases"
            )
        }
    }

    override suspend fun restorePurchases(): BillingResult {
        return try {
            val purchases = getActivePurchases().getOrNull()
            if (purchases != null) {
                // Update premium status based on restored purchases
                val currentTier = getCurrentPremiumTier()
                if (currentTier != PremiumTier.FREE) {
                    val subscription = PremiumSubscription.getPremiumIndividual(LocalDateTime.now())
                        .copy(tier = currentTier)
                    premiumRepository.updatePremiumSubscription(subscription)
                }
                
                BillingResult(
                    responseCode = BillingResponseCode.OK,
                    debugMessage = "Purchases restored successfully"
                )
            } else {
                BillingResult(
                    responseCode = BillingResponseCode.ERROR,
                    debugMessage = "Failed to restore purchases"
                )
            }
        } catch (e: Exception) {
            BillingResult(
                responseCode = BillingResponseCode.ERROR,
                debugMessage = e.message ?: "Error restoring purchases"
            )
        }
    }

    override suspend fun getProductPrice(productId: String): Result<PriceInfo> {
        return try {
            val productDetails = getProductDetails(productId)
                ?: return Result.failure(Exception("Product not found"))
            
            val priceInfo = if (productDetails.subscriptionOfferDetails?.isNotEmpty() == true) {
                val offer = productDetails.subscriptionOfferDetails!!.first()
                val pricingPhase = offer.pricingPhases.pricingPhaseList.first()
                
                PriceInfo(
                    formattedPrice = pricingPhase.formattedPrice,
                    priceAmountMicros = pricingPhase.priceAmountMicros,
                    priceCurrencyCode = pricingPhase.priceCurrencyCode,
                    countryCode = "US" // Would need to detect user's country
                )
            } else {
                productDetails.oneTimePurchaseOfferDetails?.let { offer ->
                    PriceInfo(
                        formattedPrice = offer.formattedPrice,
                        priceAmountMicros = offer.priceAmountMicros,
                        priceCurrencyCode = offer.priceCurrencyCode,
                        countryCode = "US"
                    )
                } ?: return Result.failure(Exception("No pricing information available"))
            }
            
            Result.success(priceInfo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isBillingSupported(): Boolean {
        return billingClient?.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)?.responseCode == BillingClient.BillingResponseCode.OK
    }

    override suspend fun getAvailablePaymentMethods(): Result<List<PaymentMethod>> {
        // Google Play doesn't expose detailed payment method info to apps
        // This is a limitation of the platform for security reasons
        return Result.success(listOf(
            PaymentMethod(
                type = PaymentType.GOOGLE_PAY,
                displayName = "Google Pay",
                isDefault = true
            )
        ))
    }

    override fun cancelSubscription(productId: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/account/subscriptions?sku=$productId&package=${context.packageName}")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    override fun manageSubscription(productId: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/account/subscriptions")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    override suspend fun getSubscriptionRenewalDate(productId: String): Result<Long> {
        // This would typically require server-side validation with Google Play Developer API
        // For now, return a placeholder
        return Result.failure(Exception("Renewal date requires server-side validation"))
    }

    override suspend fun isSubscriptionInGracePeriod(productId: String): Boolean {
        // This would require server-side validation
        return false
    }

    override suspend fun getAvailableOffers(productId: String): Result<List<SubscriptionOffer>> {
        return try {
            val productDetails = getProductDetails(productId)
                ?: return Result.failure(Exception("Product not found"))
            
            val offers = productDetails.subscriptionOfferDetails?.map { offer ->
                SubscriptionOffer(
                    offerId = offer.offerId ?: "base",
                    basePlanId = offer.basePlanId,
                    pricingPhases = offer.pricingPhases.pricingPhaseList.map { phase ->
                        PricingPhase(
                            formattedPrice = phase.formattedPrice,
                            priceAmountMicros = phase.priceAmountMicros,
                            priceCurrencyCode = phase.priceCurrencyCode,
                            billingPeriod = phase.billingPeriod,
                            billingCycleCount = phase.billingCycleCount,
                            recurrenceMode = when (phase.recurrenceMode) {
                                1 -> RecurrenceMode.INFINITE_RECURRING
                                2 -> RecurrenceMode.FINITE_RECURRING
                                else -> RecurrenceMode.NON_RECURRING
                            }
                        )
                    },
                    offerTags = offer.offerTags
                )
            } ?: emptyList()
            
            Result.success(offers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun validatePurchase(purchase: Purchase): Result<Boolean> {
        // This would typically involve server-side validation
        // For now, just check basic purchase validity
        return Result.success(
            purchase.purchaseState == PurchaseState.PURCHASED &&
            purchase.isAcknowledged
        )
    }

    override suspend fun getPurchaseHistory(): Result<List<Purchase>> {
        // Google Play Billing doesn't provide purchase history directly
        // This would need to be maintained on your backend
        return Result.success(emptyList())
    }

    override fun endConnection() {
        billingClient?.endConnection()
        _billingConnectionState.value = false
    }

    // Private helper methods
    
    private suspend fun queryProductDetails(params: QueryProductDetailsParams): Result<List<ProductDetails>> {
        return suspendCancellableCoroutine { continuation ->
            billingClient!!.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    continuation.resume(Result.success(productDetailsList))
                } else {
                    continuation.resume(Result.failure(Exception(billingResult.debugMessage)))
                }
            }
        }
    }

    private suspend fun getProductDetails(productId: String): ProductDetails? {
        val isSubscription = BillingProducts.ALL_SUBSCRIPTION_PRODUCTS.contains(productId)
        val productType = if (isSubscription) BillingClient.ProductType.SUBS else BillingClient.ProductType.INAPP
        
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(productType)
                        .build()
                )
            )
            .build()
        
        return queryProductDetails(params).getOrNull()?.firstOrNull()
    }

    private suspend fun getActivePurchaseForProduct(productId: String): com.android.billingclient.api.Purchase? {
        val purchases = getActivePurchases().getOrNull() ?: return null
        return purchases.find { it.productId == productId }?.let { domainPurchase ->
            // Convert back to Google Play Purchase for API calls
            // This is a simplified conversion - in practice you'd maintain the original objects
            null // Placeholder
        }
    }

    private fun handleNewPurchase(purchase: Purchase) {
        // Update premium subscription status
        try {
            val tier = BillingProducts.getProductTier(purchase.productId)
            if (tier != PremiumTier.FREE) {
                val subscription = when (tier) {
                    PremiumTier.PREMIUM_INDIVIDUAL -> PremiumSubscription.getPremiumIndividual(LocalDateTime.now())
                    PremiumTier.PREMIUM_FAMILY -> PremiumSubscription.getPremiumFamily(LocalDateTime.now())
                    PremiumTier.PREMIUM_PRO -> PremiumSubscription.getPremiumPro(LocalDateTime.now())
                    else -> return
                }
                
                // Update subscription in repository
                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                    premiumRepository.updatePremiumSubscription(subscription)
                }
            }
        } catch (e: Exception) {
            // Log error but don't block the purchase flow
        }
    }

    // Extension functions for type conversion
    private fun com.android.billingclient.api.BillingResult.toBillingResult(): BillingResult {
        return BillingResult(
            responseCode = when (responseCode) {
                BillingClient.BillingResponseCode.OK -> BillingResponseCode.OK
                BillingClient.BillingResponseCode.USER_CANCELED -> BillingResponseCode.USER_CANCELED
                BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> BillingResponseCode.SERVICE_UNAVAILABLE
                BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> BillingResponseCode.BILLING_UNAVAILABLE
                BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> BillingResponseCode.ITEM_UNAVAILABLE
                BillingClient.BillingResponseCode.DEVELOPER_ERROR -> BillingResponseCode.DEVELOPER_ERROR
                BillingClient.BillingResponseCode.ERROR -> BillingResponseCode.ERROR
                BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> BillingResponseCode.ITEM_ALREADY_OWNED
                BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> BillingResponseCode.ITEM_NOT_OWNED
                BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> BillingResponseCode.SERVICE_DISCONNECTED
                BillingClient.BillingResponseCode.NETWORK_ERROR -> BillingResponseCode.NETWORK_ERROR
                BillingClient.BillingResponseCode.SERVICE_TIMEOUT -> BillingResponseCode.SERVICE_TIMEOUT
                BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED -> BillingResponseCode.FEATURE_NOT_SUPPORTED
                else -> BillingResponseCode.ERROR
            },
            debugMessage = debugMessage
        )
    }

    private fun ProductDetails.toBillingProduct(type: ProductType): BillingProduct {
        return BillingProduct(
            productId = productId,
            type = type,
            title = title,
            description = description,
            price = if (type == ProductType.SUBSCRIPTION) {
                subscriptionOfferDetails?.firstOrNull()?.pricingPhases?.pricingPhaseList?.firstOrNull()?.formattedPrice ?: ""
            } else {
                oneTimePurchaseOfferDetails?.formattedPrice ?: ""
            },
            priceAmountMicros = if (type == ProductType.SUBSCRIPTION) {
                subscriptionOfferDetails?.firstOrNull()?.pricingPhases?.pricingPhaseList?.firstOrNull()?.priceAmountMicros ?: 0L
            } else {
                oneTimePurchaseOfferDetails?.priceAmountMicros ?: 0L
            },
            priceCurrencyCode = if (type == ProductType.SUBSCRIPTION) {
                subscriptionOfferDetails?.firstOrNull()?.pricingPhases?.pricingPhaseList?.firstOrNull()?.priceCurrencyCode ?: ""
            } else {
                oneTimePurchaseOfferDetails?.priceCurrencyCode ?: ""
            },
            subscriptionPeriod = if (type == ProductType.SUBSCRIPTION) {
                subscriptionOfferDetails?.firstOrNull()?.pricingPhases?.pricingPhaseList?.firstOrNull()?.billingPeriod
            } else null
        )
    }

    private fun com.android.billingclient.api.Purchase.toPurchase(): Purchase {
        return Purchase(
            purchaseToken = purchaseToken,
            orderId = orderId ?: "",
            packageName = packageName,
            productId = products.firstOrNull() ?: "",
            purchaseTime = purchaseTime,
            purchaseState = when (purchaseState) {
                com.android.billingclient.api.Purchase.PurchaseState.PURCHASED -> PurchaseState.PURCHASED
                com.android.billingclient.api.Purchase.PurchaseState.PENDING -> PurchaseState.PENDING
                else -> PurchaseState.UNSPECIFIED_STATE
            },
            isAcknowledged = isAcknowledged,
            isAutoRenewing = isAutoRenewing,
            signature = signature,
            originalJson = originalJson
        )
    }
}