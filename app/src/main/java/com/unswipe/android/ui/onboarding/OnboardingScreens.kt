package com.unswipe.android.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.unswipe.android.ui.theme.*

@Composable
fun OnboardingWakeupScreen(
    onNavigateToNext: () -> Unit,
    onSkip: () -> Unit
) {
    OnboardingTemplate(
        icon = "🌅",
        title = "When do you wake up?",
        subtitle = "Help us understand your daily routine to provide personalized insights",
        onNext = onNavigateToNext,
        onSkip = onSkip,
        step = 1,
        totalSteps = 4
    )
}

@Composable
fun OnboardingWorkScreen(
    onNavigateToNext: () -> Unit,
    onBack: () -> Unit
) {
    OnboardingTemplate(
        icon = "💼",
        title = "Work Schedule",
        subtitle = "When do you typically start and end work? This helps us adjust your focus periods.",
        onNext = onNavigateToNext,
        onBack = onBack,
        step = 2,
        totalSteps = 4
    )
}

@Composable
fun OnboardingSleepScreen(
    onNavigateToNext: () -> Unit,
    onBack: () -> Unit
) {
    OnboardingTemplate(
        icon = "🌙",
        title = "Sleep Schedule",
        subtitle = "When do you usually go to bed? We'll help you wind down with smart restrictions.",
        onNext = onNavigateToNext,
        onBack = onBack,
        step = 3,
        totalSteps = 4
    )
}

@Composable
fun OnboardingPermissionsScreen(
    onNavigateToLogin: () -> Unit,
    onBack: () -> Unit
) {
    OnboardingTemplate(
        icon = "🔐",
        title = "Permissions Setup",
        subtitle = "We need a few permissions to help you track and control your app usage effectively.",
        onNext = onNavigateToLogin,
        onBack = onBack,
        step = 4,
        totalSteps = 4,
        nextButtonText = "Get Started"
    )
}

@Composable
private fun OnboardingTemplate(
    icon: String,
    title: String,
    subtitle: String,
    onNext: () -> Unit,
    onBack: (() -> Unit)? = null,
    onSkip: (() -> Unit)? = null,
    step: Int,
    totalSteps: Int,
    nextButtonText: String = "Continue"
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        UnswipeBlack,
                        UnswipeSurface
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Skip button
            if (onSkip != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onSkip) {
                        Text(
                            text = "Skip",
                            color = UnswipeTextSecondary
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(48.dp))
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Progress indicator
            LinearProgressIndicator(
                progress = { step.toFloat() / totalSteps },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = UnswipePrimary,
                trackColor = UnswipeGray.copy(alpha = 0.3f),
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Icon
            Text(
                text = icon,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 64.sp
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = UnswipeTextPrimary,
                    fontSize = 28.sp
                ),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Subtitle
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = UnswipeTextSecondary,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (onBack != null) Arrangement.SpaceBetween else Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBack != null) {
                    OutlinedButton(
                        onClick = onBack,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = UnswipeTextSecondary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Back")
                    }
                }
                
                Button(
                    onClick = onNext,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UnswipePrimary,
                        contentColor = UnswipeBlack
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .then(
                            if (onBack == null) Modifier.fillMaxWidth() else Modifier.weight(1f).padding(start = 16.dp)
                        )
                        .height(56.dp)
                ) {
                    Text(
                        text = nextButtonText,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
} 