package com.unswipe.android.ui.dashboard;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0014\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\f0\u000bH\u0002J\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0002J\u0012\u0010\u0011\u001a\u00020\u00072\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013H\u0002R\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/unswipe/android/ui/dashboard/DashboardViewModel;", "Landroidx/lifecycle/ViewModel;", "usageRepository", "Lcom/unswipe/android/domain/repository/UsageRepository;", "(Lcom/unswipe/android/domain/repository/UsageRepository;)V", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "Lcom/unswipe/android/ui/dashboard/DashboardUiState;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "checkPermissions", "Lkotlin/Pair;", "", "formatMillis", "", "millis", "", "mapDomainToUiState", "domainData", "Lcom/unswipe/android/domain/model/DashboardData;", "app_debug"})
public final class DashboardViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.unswipe.android.domain.repository.UsageRepository usageRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.unswipe.android.ui.dashboard.DashboardUiState> uiState = null;
    
    @javax.inject.Inject()
    public DashboardViewModel(@org.jetbrains.annotations.NotNull()
    com.unswipe.android.domain.repository.UsageRepository usageRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.unswipe.android.ui.dashboard.DashboardUiState> getUiState() {
        return null;
    }
    
    private final com.unswipe.android.ui.dashboard.DashboardUiState mapDomainToUiState(com.unswipe.android.domain.model.DashboardData domainData) {
        return null;
    }
    
    private final java.lang.String formatMillis(long millis) {
        return null;
    }
    
    private final kotlin.Pair<java.lang.Boolean, java.lang.Boolean> checkPermissions() {
        return null;
    }
}