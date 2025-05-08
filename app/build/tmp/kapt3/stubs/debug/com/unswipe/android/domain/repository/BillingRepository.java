package com.unswipe.android.domain.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\u00020\u0003H\u00a6@\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H&J\u0018\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\n\u001a\u00020\u000bH\u00a6@\u00a2\u0006\u0002\u0010\fJ\u0018\u0010\r\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\tH&\u00a8\u0006\u0011"}, d2 = {"Lcom/unswipe/android/domain/repository/BillingRepository;", "", "checkSubscriptions", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getPremiumStatusFlow", "Lkotlinx/coroutines/flow/Flow;", "", "getSubscriptionProductDetails", "Lcom/android/billingclient/api/ProductDetails;", "productId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "launchPurchaseFlow", "activity", "Landroid/app/Activity;", "productDetails", "app_debug"})
public abstract interface BillingRepository {
    
    /**
     * Provides a Flow indicating the user's current premium subscription status (true if active, false otherwise).
     * This should be the authoritative source for premium status.
     */
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.lang.Boolean> getPremiumStatusFlow();
    
    /**
     * Fetches the product details for a given subscription ID from Google Play.
     * Returns null if not found or an error occurs.
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getSubscriptionProductDetails(@org.jetbrains.annotations.NotNull()
    java.lang.String productId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.android.billingclient.api.ProductDetails> $completion);
    
    /**
     * Initiates the Google Play purchase flow for the given product details.
     * Requires the current Activity to launch the billing UI.
     */
    public abstract void launchPurchaseFlow(@org.jetbrains.annotations.NotNull()
    android.app.Activity activity, @org.jetbrains.annotations.NotNull()
    com.android.billingclient.api.ProductDetails productDetails);
    
    /**
     * Manually triggers a check/refresh of the user's current subscriptions with Google Play.
     * Useful to call on app resume or specific events.
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object checkSubscriptions(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}