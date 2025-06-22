package com.unswipe.android.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unswipe.android.ui.MainActivity
import com.unswipe.android.ui.theme.UnswipeTheme
import kotlinx.coroutines.delay
import androidx.core.view.WindowCompat // Import for WindowCompat

// Colors from Figma specs
val filledCircleColor = Color(red = 0.827f, green = 0.827f, blue = 1f, alpha = 0.08f)
val outlinedCircleColor = Color(red = 1f, green = 1f, blue = 1f, alpha = 0.08f) // Combined 0.8 alpha stroke * 0.1 opacity

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
    SplashScreen()
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Outlined Decorative Circles (largest to smallest)
        val outlinedCircleSizes = listOf(1263.dp, 1021.dp, 799.dp, 555.dp)
        outlinedCircleSizes.forEach { size ->
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape) // Clip to ensure border is circular
                    .border(BorderStroke(1.5.dp, outlinedCircleColor), CircleShape)
            )
        }

        // Filled Branding Circles (largest to smallest for correct layering)
        val filledCircleSizes = listOf(455.dp, 343.dp, 269.dp)
        filledCircleSizes.forEach { size ->
            Box(
                modifier = Modifier
                    .size(size)
                    .background(color = filledCircleColor, shape = CircleShape)
            )
        }

        Text(
            text = "UNSWIPE", // Typo warning can be suppressed here or via dictionary
            style = TextStyle(
                fontFamily = FontFamily.SansSerif, // Defaults to Roboto
                fontWeight = FontWeight.Normal,   // For Roboto Regular
                fontSize = 46.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.align(Alignment.Center) // Ensure text is perfectly centered
        )
    }
}

@Preview(showBackground = true, device = "spec:width=375dp,height=812dp,dpi=480")
@Composable
fun SplashScreenPreview() {
    UnswipeTheme {  // Restore the theme
        SplashScreen() // Restore the actual splash screen
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