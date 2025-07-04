package com.unswipe.android.ui.settings

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.unswipe.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contact Support", color = UnswipeTextPrimary) },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                
                Icon(
                    imageVector = Icons.Default.HeadsetMic,
                    contentDescription = null,
                    tint = UnswipePrimary,
                    modifier = Modifier.size(80.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "We're Here to Help",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = UnswipeTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
                
                Text(
                    text = "Choose how you'd like to reach us",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = UnswipeTextSecondary,
                        textAlign = TextAlign.Center
                    )
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Email support
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = UnswipeCard
                    ),
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                            data = android.net.Uri.parse("mailto:support@unswipe.app")
                            putExtra(android.content.Intent.EXTRA_SUBJECT, "Unswipe Support Request")
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = UnswipePrimary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = UnswipePrimary
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Email Support",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = UnswipeTextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Text(
                                text = "support@unswipe.app",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = UnswipeTextSecondary
                                )
                            )
                        }
                        
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = UnswipeTextSecondary
                        )
                    }
                }
                
                // Response time info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = UnswipePrimary.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = UnswipePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Text(
                            text = "We typically respond within 24 hours",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = UnswipePrimary
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Version info
                Text(
                    text = "App Version: 1.0.0",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = UnswipeTextSecondary
                    )
                )
            }
        }
    }
}