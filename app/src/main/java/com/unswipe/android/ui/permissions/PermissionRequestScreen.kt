package com.unswipe.android.ui.permissions

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionRequestScreen(
    onNavigateNext: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: PermissionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // Header
        Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Permissions Required",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "To help you track and limit your social media usage, Unswipe needs access to specific device features.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Usage Stats Permission
        PermissionCard(
            icon = Icons.Default.Analytics,
            title = "Usage Statistics",
            description = "Track time spent on social media apps like Instagram, TikTok, and YouTube",
            isGranted = uiState.hasUsageStatsPermission,
            onRequestPermission = {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                context.startActivity(intent)
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Accessibility Permission
        PermissionCard(
            icon = Icons.Default.Accessibility,
            title = "Accessibility Service",
            description = "Show confirmation dialogs when you try to open blocked apps",
            isGranted = uiState.isAccessibilityEnabled,
            onRequestPermission = {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                context.startActivity(intent)
            }
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Continue Button
        Button(
            onClick = onNavigateNext,
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.hasUsageStatsPermission && uiState.isAccessibilityEnabled
        ) {
            Text("Continue")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        TextButton(onClick = onNavigateBack) {
            Text("Back")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
    
    // Check permissions when screen becomes visible
    LaunchedEffect(Unit) {
        viewModel.checkPermissions()
    }
}

@Composable
private fun PermissionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    isGranted: Boolean,
    onRequestPermission: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isGranted) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (isGranted) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (isGranted) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                if (isGranted) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Granted",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isGranted) 
                    MaterialTheme.colorScheme.onPrimaryContainer 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (!isGranted) {
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedButton(
                    onClick = onRequestPermission,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Grant Permission")
                }
            }
        }
    }
}