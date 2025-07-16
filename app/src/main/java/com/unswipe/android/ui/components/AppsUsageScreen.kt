package com.unswipe.android.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import com.unswipe.android.data.apps.AppInfo
import com.unswipe.android.ui.theme.*

@Composable
fun AppsUsageScreen(
    viewModel: AppsUsageViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddAppDialog by remember { mutableStateOf(false) }
    var availableApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var isLoadingApps by remember { mutableStateOf(false) }
    
    // Load available apps when dialog opens
    LaunchedEffect(showAddAppDialog) {
        if (showAddAppDialog) {
            isLoadingApps = true
            availableApps = viewModel.getAvailableAppsForSelection()
            isLoadingApps = false
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MinimalistWhite)
    ) {
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color = MinimalistBlack,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    ErrorMessage(error = uiState.error!!)
                }
            }
            else -> {
                AppsUsageContent(
                    apps = uiState.apps,
                    onAppToggle = viewModel::toggleAppTracking,
                    onRefresh = viewModel::refreshApps,
                    onAddApp = { showAddAppDialog = true }
                )
            }
        }
        
        if (showAddAppDialog) {
            AddAppDialog(
                availableApps = availableApps,
                isLoading = isLoadingApps,
                onAppSelected = { packageName ->
                    viewModel.addAppToTracking(packageName)
                    showAddAppDialog = false
                },
                onDismiss = { showAddAppDialog = false }
            )
        }
    }
}

@Composable
private fun AppsUsageContent(
    apps: List<AppUsageItem>,
    onAppToggle: (String) -> Unit,
    onRefresh: () -> Unit = {},
    onAddApp: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            
            // Apps Header
            Text(
                text = "Apps",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Light,
                    color = MinimalistBlack
                )
            )
            
            Text(
                text = "Select apps to track and manage",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MinimalistBlack,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            )
        }
        
        item {
            // Add App Button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(1.dp),
                color = MinimalistBlack,
                shape = RoundedCornerShape(0.dp),
                shadowElevation = 0.dp
            ) {
                TextButton(
                    onClick = onAddApp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add App",
                            tint = MinimalistWhite,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add App",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = MinimalistWhite,
                                fontSize = 16.sp
                            )
                        )
                    }
                }
            }
        }
        
        items(apps) { app ->
            AppUsageCard(
                app = app,
                onToggle = { onAppToggle(app.packageName) }
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(100.dp)) // Bottom padding
        }
    }
}

@Composable
private fun AppUsageCard(
    app: AppUsageItem,
    onToggle: () -> Unit
) {
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // App icon
                Surface(
                    modifier = Modifier.size(48.dp),
                    color = MinimalistWhite,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        val appIcon = remember(app.appInfo.icon) {
                            try {
                                app.appInfo.icon.toBitmap(48, 48)
                            } catch (e: Exception) {
                                null
                            }
                        }
                        
                        if (appIcon != null) {
                            Image(
                                bitmap = appIcon.asImageBitmap(),
                                contentDescription = app.appName,
                                modifier = Modifier.size(40.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Android,
                                contentDescription = null,
                                tint = MinimalistBlack,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                
                // App info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = app.appName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Normal,
                            color = MinimalistBlack,
                            fontSize = 16.sp
                        )
                    )
                    
                    Text(
                        text = app.usageTime,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MinimalistBlack,
                            fontSize = 14.sp
                        )
                    )
                }
                
                // Toggle switch
                Switch(
                    checked = app.isTracked,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MinimalistWhite,
                        checkedTrackColor = MinimalistBlack,
                        uncheckedThumbColor = MinimalistBlack,
                        uncheckedTrackColor = MinimalistWhite
                    )
                )
            }
        }
    }
}

@Composable
private fun ErrorMessage(error: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        color = MinimalistBlack,
        shape = RoundedCornerShape(0.dp),
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Error",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MinimalistWhite,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MinimalistWhite
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AddAppDialog(
    availableApps: List<AppInfo>,
    isLoading: Boolean = false,
    onAppSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add App to Track",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MinimalistBlack,
                    fontWeight = FontWeight.Normal
                )
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MinimalistBlack,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                } else if (availableApps.isEmpty()) {
                    item {
                        Text(
                            text = "No apps available to add",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MinimalistBlack,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                } else {
                    items(availableApps) { app ->
                        AppSelectionItem(
                            app = app,
                            onSelected = { onAppSelected(app.packageName) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = MinimalistBlack
                )
            }
        }
    )
}

@Composable
private fun AppSelectionItem(
    app: AppInfo,
    onSelected: () -> Unit
) {
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
                .padding(1.dp),
            color = MinimalistWhite,
            shape = RoundedCornerShape(0.dp)
        ) {
            TextButton(
                onClick = onSelected,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // App icon
                    Box(
                        modifier = Modifier.size(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val appIcon = remember(app.icon) {
                            try {
                                app.icon.toBitmap(32, 32)
                            } catch (e: Exception) {
                                null
                            }
                        }
                        
                        if (appIcon != null) {
                            Image(
                                bitmap = appIcon.asImageBitmap(),
                                contentDescription = app.appName,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Android,
                                contentDescription = null,
                                tint = MinimalistBlack,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    // App name
                    Text(
                        text = app.appName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MinimalistBlack,
                            fontSize = 14.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Add icon
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = MinimalistBlack,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}