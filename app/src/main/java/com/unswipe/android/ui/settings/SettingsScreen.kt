package com.unswipe.android.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unswipe.android.ui.navigation.Screen
import com.unswipe.android.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateTo: (String) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Function to handle rate app
    val handleRateApp = {
        try {
            val packageName = context.packageName
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                data = android.net.Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                setPackage("com.android.vending")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // If Play Store is not available, open in browser
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                data = android.net.Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
            }
            context.startActivity(intent)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MinimalistWhite)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            item {
                SettingsSection(
                    title = "Account",
                    items = listOf(
                        SettingsItemData("Reset Password", Icons.Default.Lock, onClick = { onNavigateTo(Screen.ResetPassword.route) }),
                        SettingsItemData("Notifications", Icons.Default.Notifications, onClick = { onNavigateTo(Screen.NotificationSettings.route) })
                    )
                )
            }
            
            item {
                SettingsSection(
                    title = "App Controls",
                    items = listOf(
                        SettingsItemData("Daily Limit", Icons.Default.Schedule, onClick = { onNavigateTo(Screen.DailyLimit.route) }),
                        SettingsItemData("Focus Mode", Icons.Default.DoNotDisturb, onClick = { onNavigateTo(Screen.FocusMode.route) }),
                        SettingsItemData("Rules", Icons.Default.Rule, onClick = { onNavigateTo(Screen.Rules.route) })
                    )
                )
            }
            
            item {
                SettingsSection(
                    title = "Premium",
                    items = listOf(
                        SettingsItemData("Upgrade to Premium", Icons.Default.Star, isPremium = true, onClick = { onNavigateTo(Screen.Premium.route) }),
                        SettingsItemData("Manage Subscription", Icons.Default.CreditCard, onClick = { onNavigateTo(Screen.SubscriptionManagement.route) })
                    )
                )
            }
            
            item {
                SettingsSection(
                    title = "Support",
                    items = listOf(
                        SettingsItemData("Help & FAQ", Icons.Default.Help, onClick = { onNavigateTo(Screen.Help.route) }),
                        SettingsItemData("Contact Support", Icons.Default.Email, onClick = { onNavigateTo(Screen.Support.route) }),
                        SettingsItemData("Rate App", Icons.Default.ThumbUp, onClick = { handleRateApp() })
                    )
                )
            }
            
            item {
                SettingsSection(
                    title = "Account Actions",
                    items = listOf(
                        SettingsItemData("Logout", Icons.Default.ExitToApp, isDestructive = true, onClick = onLogout),
                        SettingsItemData("Delete Account", Icons.Default.DeleteForever, isDestructive = true, onClick = { 
                            scope.launch {
                                viewModel.deleteAccount()
                            }
                        })
                    )
                )
            }
            
            // Add some bottom padding
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

data class SettingsItemData(
    val title: String,
    val icon: ImageVector,
    val isPremium: Boolean = false,
    val isDestructive: Boolean = false,
    val onClick: () -> Unit
)


@Composable
private fun SettingsSection(
    title: String,
    items: List<SettingsItemData>
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                color = MinimalistBlack,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )
        
        // Use minimalist design consistent with the rest of the app
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(1.dp),
            color = MinimalistBlack,
            shape = RoundedCornerShape(0.dp),
            shadowElevation = 0.dp
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
                color = MinimalistWhite,
                shape = RoundedCornerShape(0.dp)
            ) {
                Column {
                    items.forEachIndexed { index, item ->
                        ModernSettingsItem(
                            item = item,
                            showDivider = index < items.size - 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernSettingsItem(
    item: SettingsItemData,
    showDivider: Boolean = true
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = item.onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon background - consistent minimalist design
            Surface(
                modifier = Modifier.size(32.dp),
                color = MinimalistBlack,
                shape = RoundedCornerShape(0.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = MinimalistWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MinimalistBlack,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.weight(1f)
            )
            
            if (item.isPremium) {
                Surface(
                    color = MinimalistBlack,
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text(
                        text = "PRO",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MinimalistWhite,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            if (!item.isDestructive) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = MinimalistBlack.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MinimalistBlack.copy(alpha = 0.1f)
            )
        }
    }
} 