package com.unswipe.android.domain.model

import java.time.LocalDateTime

/**
 * Billing product information from Google Play
 */
data class BillingProduct(
    val productId: String,
    val type: ProductType,
    val title: String,
    val description: String,
    val price: String,
    val priceAmountMicros: Long,
    val priceCurrencyCode: String,
    val subscriptionPeriod: String? = null, // P1M for monthly, P1Y for yearly
    val freeTrialPeriod: String? = null,    // P7D for 7-day trial
    val introductoryPrice: String? = null,
    val introductoryPriceAmountMicros: Long? = null,
    val introductoryPricePeriod: String? = null
)

enum class ProductType {
    SUBSCRIPTION,
    ONE_TIME_PURCHASE
}

/**
 * Purchase information from Google Play
 */
data class Purchase(
    val purchaseToken: String,
    val orderId: String,
    val packageName: String,
    val productId: String,
    val purchaseTime: Long,
    val purchaseState: PurchaseState,
    val isAcknowledged: Boolean,
    val isAutoRenewing: Boolean,
    val signature: String,
    val originalJson: String
)

enum class PurchaseState {
    UNSPECIFIED_STATE,
    PURCHASED,
    PENDING
}

/**
 * Billing result from operations
 */
data class BillingResult(
    val responseCode: BillingResponseCode,
    val debugMessage: String
) {
    val isSuccess: Boolean
        get() = responseCode == BillingResponseCode.OK
}

enum class BillingResponseCode {
    SERVICE_TIMEOUT,
    FEATURE_NOT_SUPPORTED,
    SERVICE_DISCONNECTED,
    OK,
    USER_CANCELED,
    SERVICE_UNAVAILABLE,
    BILLING_UNAVAILABLE,
    ITEM_UNAVAILABLE,
    DEVELOPER_ERROR,
    ERROR,
    ITEM_ALREADY_OWNED,
    ITEM_NOT_OWNED,
    NETWORK_ERROR
}

/**
 * Subscription status information
 */
data class SubscriptionStatus(
    val productId: String,
    val isActive: Boolean,
    val isExpired: Boolean,
    val expiryTime: LocalDateTime?,
    val autoRenewing: Boolean,
    val purchaseToken: String,
    val orderId: String?,
    val isInGracePeriod: Boolean = false,
    val isOnHold: Boolean = false,
    val isPaused: Boolean = false
)

/**
 * Payment method information
 */
data class PaymentMethod(
    val type: PaymentType,
    val displayName: String,
    val lastFourDigits: String? = null,
    val expiryMonth: Int? = null,
    val expiryYear: Int? = null,
    val isDefault: Boolean = false
)

enum class PaymentType {
    CREDIT_CARD,
    DEBIT_CARD,
    PAYPAL,
    GOOGLE_PAY,
    CARRIER_BILLING,
    GIFT_CARD,
    GOOGLE_PLAY_BALANCE,
    UNKNOWN
}

/**
 * Billing error information
 */
data class BillingError(
    val code: BillingResponseCode,
    val message: String,
    val debugMessage: String? = null,
    val isRetryable: Boolean = false
)

/**
 * Price information with localization
 */
data class PriceInfo(
    val formattedPrice: String,        // "$4.99"
    val priceAmountMicros: Long,       // 4990000 (price in micros)
    val priceCurrencyCode: String,     // "USD"
    val countryCode: String,           // "US"
    val originalPrice: String? = null, // For promotional pricing
    val discountPercentage: Int? = null
)

/**
 * Subscription offer details
 */
data class SubscriptionOffer(
    val offerId: String,
    val basePlanId: String,
    val pricingPhases: List<PricingPhase>,
    val offerTags: List<String> = emptyList()
)

data class PricingPhase(
    val formattedPrice: String,
    val priceAmountMicros: Long,
    val priceCurrencyCode: String,
    val billingPeriod: String,          // P1M, P1Y, etc.
    val billingCycleCount: Int,         // Number of cycles (0 = infinite)
    val recurrenceMode: RecurrenceMode
)

enum class RecurrenceMode {
    INFINITE_RECURRING,
    FINITE_RECURRING,
    NON_RECURRING
}

/**
 * Product configuration for our app
 */
object BillingProducts {
    const val PREMIUM_INDIVIDUAL_MONTHLY = "premium_individual_monthly"
    const val PREMIUM_INDIVIDUAL_YEARLY = "premium_individual_yearly"
    const val PREMIUM_FAMILY_MONTHLY = "premium_family_monthly"
    const val PREMIUM_FAMILY_YEARLY = "premium_family_yearly"
    const val PREMIUM_PRO_MONTHLY = "premium_pro_monthly"
    const val PREMIUM_PRO_YEARLY = "premium_pro_yearly"
    
    // One-time purchases
    const val LIFETIME_PREMIUM = "lifetime_premium"
    const val EXTRA_BYPASS_CREDITS = "extra_bypass_credits_50"
    
    val ALL_SUBSCRIPTION_PRODUCTS = listOf(
        PREMIUM_INDIVIDUAL_MONTHLY,
        PREMIUM_INDIVIDUAL_YEARLY,
        PREMIUM_FAMILY_MONTHLY,
        PREMIUM_FAMILY_YEARLY,
        PREMIUM_PRO_MONTHLY,
        PREMIUM_PRO_YEARLY
    )
    
    val ALL_ONE_TIME_PRODUCTS = listOf(
        LIFETIME_PREMIUM,
        EXTRA_BYPASS_CREDITS
    )
    
    fun getProductTier(productId: String): PremiumTier {
        return when {
            productId.contains("individual") -> PremiumTier.PREMIUM_INDIVIDUAL
            productId.contains("family") -> PremiumTier.PREMIUM_FAMILY
            productId.contains("pro") -> PremiumTier.PREMIUM_PRO
            productId == LIFETIME_PREMIUM -> PremiumTier.PREMIUM_PRO
            else -> PremiumTier.FREE
        }
    }
    
    fun isYearlyProduct(productId: String): Boolean {
        return productId.contains("yearly")
    }
}