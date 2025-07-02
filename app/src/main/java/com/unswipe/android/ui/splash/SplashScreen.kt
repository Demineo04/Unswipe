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

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToDashboard: () -> Unit
) {
    // Simulate checking user state
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        delay(2000) // Show splash for 2 seconds
        isLoading = false
        
        // For now, always go to onboarding to simulate first launch
        // In a real app, you'd check SharedPreferences or user authentication state
        onNavigateToOnboarding()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        UnswipeBlack,
                        UnswipeSurface,
                        UnswipeCard
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo
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
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Unswipe",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 32.sp,
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
            
            Spacer(modifier = Modifier.height(40.dp))
            
            if (isLoading) {
                CircularProgressIndicator(
                    color = UnswipePrimary,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
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