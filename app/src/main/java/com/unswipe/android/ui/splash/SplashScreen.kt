package com.unswipe.android.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unswipe.android.ui.MainActivity
import com.unswipe.android.ui.theme.*
import kotlinx.coroutines.delay
import androidx.core.view.WindowCompat // Import for WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unswipe.android.domain.repository.AuthRepository
import com.unswipe.android.domain.repository.OnboardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Colors for modern design
// Colors are now defined in theme files

@SuppressLint("CustomSplashScreen") // Suppress the warning about providing our own launch screen
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make status bar transparent and content draw behind it
        // This is generally preferred for edge-to-edge UIs.
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            // Optional: If using Accompanist System UI Controller, you could further customize here.
            // For this simple case, ensuring the theme handles dark status bar icons is often enough,
            // or the system automatically adapts for dark backgrounds.
            // If icons are still dark, we might need: systemUiController.statusBarDarkContentEnabled = false

            UnswipeTheme { // Apply your app\'s theme
                SplashScreenWithNavigation()
            }
        }
    }
}

@Composable
fun SplashScreenWithNavigation() {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        delay(3000) // 3-second delay
        context.startActivity(Intent(context, MainActivity::class.java))
        // Finish SplashActivity so user can\'t navigate back to it
        (context as? ComponentActivity)?.finish()
    }
    SplashScreen(
        onNavigateToOnboarding = {
            context.startActivity(Intent(context, MainActivity::class.java))
            (context as? ComponentActivity)?.finish()
        },
        onNavigateToLogin = { /* Implementation needed */ },
        onNavigateToDashboard = { /* Implementation needed */ }
    )
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val onboardingRepository: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        checkUserStatus()
    }

    private fun checkUserStatus() {
        viewModelScope.launch {
            try {
                // Add a minimum splash duration for better UX
                delay(1500)
                
                // Check authentication status
                val currentUser = authRepository.getCurrentUser()
                val isAuthenticated = currentUser != null
                
                if (isAuthenticated) {
                    // User is authenticated, check onboarding status
                    val isOnboardingComplete = onboardingRepository.isOnboardingComplete()
                    
                    if (isOnboardingComplete) {
                        // Returning user - go to dashboard
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            destination = SplashDestination.DASHBOARD
                        )
                    } else {
                        // Authenticated but incomplete onboarding - continue onboarding
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            destination = SplashDestination.ONBOARDING
                        )
                    }
                } else {
                    // User is not authenticated - go to login
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        destination = SplashDestination.LOGIN
                    )
                }
                
            } catch (e: Exception) {
                // On error, default to login screen
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    destination = SplashDestination.LOGIN,
                    error = e.localizedMessage
                )
            }
        }
    }
}

data class SplashUiState(
    val isLoading: Boolean = true,
    val destination: SplashDestination? = null,
    val error: String? = null
)

enum class SplashDestination {
    LOGIN,
    ONBOARDING, 
    DASHBOARD
}

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle navigation when destination is determined
    LaunchedEffect(uiState.destination) {
        when (uiState.destination) {
            SplashDestination.LOGIN -> onNavigateToLogin()
            SplashDestination.ONBOARDING -> onNavigateToOnboarding()
            SplashDestination.DASHBOARD -> onNavigateToDashboard()
            null -> {} // Still loading
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        UnswipeBlack,
                        UnswipeSurface,
                        UnswipeBlack
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                UnswipePrimary,
                                UnswipeSecondary
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "U",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = UnswipeBlack
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // App name
            Text(
                text = "Unswipe",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = UnswipeTextPrimary
                )
            )
            
            Text(
                text = "Take control of your digital life",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = UnswipeTextSecondary,
                    fontSize = 16.sp
                )
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Loading indicator
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = UnswipePrimary,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(36.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = UnswipeTextSecondary
                    )
                )
            }
            
            // Error message if any
            uiState.error?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = UnswipeRed.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.padding(horizontal = 32.dp)
                ) {
                    Text(
                        text = "Error: $error",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = UnswipeRed
                        ),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
        
        // Version info at bottom
        Text(
            text = "Version 1.0.0",
            style = MaterialTheme.typography.labelSmall.copy(
                color = UnswipeTextSecondary.copy(alpha = 0.6f)
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}

@Preview(showBackground = true, device = "spec:width=375dp,height=812dp,dpi=480")
@Composable
fun SplashScreenPreview() {
    UnswipeTheme {  // Restore the theme
        SplashScreen(
            onNavigateToOnboarding = { /* Preview - no navigation */ },
            onNavigateToLogin = { /* Preview - no navigation */ },
            onNavigateToDashboard = { /* Preview - no navigation */ }
        ) // Restore the actual splash screen
    }
    /* Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red), // Draw a simple red background
        contentAlignment = Alignment.Center
    ) {
        Text("Hello Preview!", fontSize = 30.sp, color = Color.White)
    } */
} 