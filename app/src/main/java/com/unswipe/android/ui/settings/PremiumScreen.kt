package com.unswipe.android.ui.settings

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unswipe.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(
    onNavigateBack: () -> Unit,
    viewModel: PremiumViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (uiState.isPremium) "Premium Status" else "Upgrade to Premium", 
                        color = UnswipeTextPrimary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = UnswipeTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = UnswipeBlack
                )
            )
        },
        containerColor = UnswipeBlack
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(UnswipeBlack, UnswipeSurface)
                    )
                )
        ) {
            if (uiState.isPremium) {
                PremiumActiveContent(
                    onManageSubscription = { viewModel.manageSubscription() }
                )
            } else {
                PremiumUpgradeContent(
                    onUpgrade = { viewModel.startPurchase() },
                    isLoading = uiState.isLoading
                )
            }
            
            // Error snackbar
            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }
}

@Composable
private fun PremiumActiveContent(
    onManageSubscription: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Success card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = UnswipePrimary.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = UnswipePrimary,
                        modifier = Modifier.size(64.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "You're Premium! 🎉",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = UnswipeTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Enjoy all premium features including AI-powered nudges and advanced analytics",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = UnswipeTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
        
        item {
            // Active features
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = UnswipeCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Active Premium Features",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = UnswipeTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    PremiumFeatureItem(
                        icon = Icons.Default.Psychology,
                        title = "AI Smart Nudges",
                        description = "Personalized interventions powered by AI",
                        isActive = true
                    )
                    
                    PremiumFeatureItem(
                        icon = Icons.Default.Analytics,
                        title = "Advanced Analytics",
                        description = "Deep insights into your usage patterns",
                        isActive = true
                    )
                    
                    PremiumFeatureItem(
                        icon = Icons.Default.Speed,
                        title = "Smart Bypass",
                        description = "Intelligent confirmation bypassing",
                        isActive = true
                    )
                    
                    PremiumFeatureItem(
                        icon = Icons.Default.CloudDownload,
                        title = "Data Export",
                        description = "Export your data anytime",
                        isActive = true
                    )
                }
            }
        }
        
        item {
            // Manage subscription button
            Button(
                onClick = onManageSubscription,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = UnswipeGray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Settings, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Manage Subscription",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun PremiumUpgradeContent(
    onUpgrade: () -> Unit,
    isLoading: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Hero section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "✨",
                    fontSize = 48.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Unlock Your Digital\nWellness Potential",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = UnswipeTextPrimary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
        
        item {
            // Pricing card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = UnswipePrimary.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(20.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.horizontalGradient(
                        colors = listOf(UnswipePrimary, UnswipeSecondary)
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Premium Monthly",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = UnswipeTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "$",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = UnswipeTextSecondary
                            )
                        )
                        Text(
                            text = "4.99",
                            style = MaterialTheme.typography.displayMedium.copy(
                                color = UnswipePrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "/month",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = UnswipeTextSecondary
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "7-day free trial • Cancel anytime",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = UnswipeTextSecondary
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = onUpgrade,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = UnswipePrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = UnswipeBlack,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "Start Free Trial",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
        
        item {
            // Features list
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = UnswipeCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Premium Features",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = UnswipeTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    PremiumFeatureItem(
                        icon = Icons.Default.Psychology,
                        title = "AI-Powered Smart Nudges",
                        description = "ChatGPT-generated contextual notifications"
                    )
                    
                    PremiumFeatureItem(
                        icon = Icons.Default.Speed,
                        title = "Smart Confirmation Bypass",
                        description = "Skip confirmations when usage is healthy"
                    )
                    
                    PremiumFeatureItem(
                        icon = Icons.Default.Analytics,
                        title = "Advanced Analytics",
                        description = "Detailed hourly breakdowns, trends, insights"
                    )
                    
                    PremiumFeatureItem(
                        icon = Icons.Default.Schedule,
                        title = "Critical Period Intelligence",
                        description = "Work hours, sleep time detection"
                    )
                    
                    PremiumFeatureItem(
                        icon = Icons.Default.Apps,
                        title = "Custom App Blocking",
                        description = "Block ANY app beyond social media"
                    )
                    
                    PremiumFeatureItem(
                        icon = Icons.Default.CloudDownload,
                        title = "Data Export",
                        description = "Export usage data for personal tracking"
                    )
                    
                    PremiumFeatureItem(
                        icon = Icons.Default.SupportAgent,
                        title = "Priority Support",
                        description = "Faster response to issues"
                    )
                }
            }
        }
        
        item {
            // Comparison
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = UnswipeCard.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = UnswipePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "✨ No Ads, Ever",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = UnswipeTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    
                    Text(
                        text = "Clean experience for all users",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = UnswipeTextSecondary
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "🔒 Secure • 🚫 No Tracking",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = UnswipeTextSecondary
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun PremiumFeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    isActive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = if (isActive) UnswipePrimary.copy(alpha = 0.1f) 
                           else UnswipeGray.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isActive) UnswipePrimary else UnswipeTextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = UnswipeTextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                )
                
                if (isActive) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = UnswipeGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = UnswipeTextSecondary
                )
            )
        }
    }
}

// ViewModel
data class PremiumUiState(
    val isPremium: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val subscriptionDetails: SubscriptionDetails? = null
)

data class SubscriptionDetails(
    val productId: String,
    val price: String,
    val renewalDate: String?
)

class PremiumViewModel @javax.inject.Inject constructor(
    private val billingRepository: com.unswipe.android.domain.repository.BillingRepository,
    private val premiumRepository: com.unswipe.android.domain.repository.PremiumRepository
) : androidx.lifecycle.ViewModel() {
    
    private val _uiState = kotlinx.coroutines.flow.MutableStateFlow(PremiumUiState())
    val uiState: kotlinx.coroutines.flow.StateFlow<PremiumUiState> = _uiState.asStateFlow()
    
    init {
        checkPremiumStatus()
    }
    
    private fun checkPremiumStatus() {
        viewModelScope.launch {
            try {
                val isPremium = premiumRepository.isPremiumUser()
                _uiState.value = _uiState.value.copy(isPremium = isPremium)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to check premium status"
                )
            }
        }
    }
    
    fun startPurchase() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val success = billingRepository.startPurchaseFlow("unswipe_premium_monthly")
                if (success) {
                    checkPremiumStatus()
                }
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Purchase failed: ${e.message}"
                )
            }
        }
    }
    
    fun manageSubscription() {
        // TODO: Open subscription management in Play Store
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}