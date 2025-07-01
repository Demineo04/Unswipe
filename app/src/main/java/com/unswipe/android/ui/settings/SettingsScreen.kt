package com.unswipe.android.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unswipe.android.ui.navigation.Screen

@Composable
fun SettingsScreen(
    // viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateTo: (String) -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Settings") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                ProfileHeader(userName = "Grace J.", userEmail = "grace@example.com")
            }
            item { SettingsItem(title = "Edit Profile") { /* onNavigateTo("edit_profile") */ } }
            item { SettingsItem(title = "Reset Password") { /* onNavigateTo("reset_password") */ } }
            item { SettingsItem(title = "Daily Limit") { onNavigateTo(Screen.DailyLimit.route) } }
            item { SettingsItem(title = "App Selection") { onNavigateTo(Screen.AppSelection.route) } }
            item { SettingsItem(title = "Upgrade to Premium") { /* onNavigateTo("premium") */ } }
            item { SettingsItem(title = "Manage Subscription") { /* onNavigateTo("subscription") */ } }
            item { Divider(modifier = Modifier.padding(vertical = 8.dp)) }
            item {
                SettingsItem(title = "Logout", isDestructive = true) {
                    // viewModel.logout()
                    onLogout()
                }
            }
            item {
                SettingsItem(title = "Delete Account", isDestructive = true) {
                    // viewModel.deleteAccount()
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(userName: String, userEmail: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Replace with a user avatar later
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = userName, style = MaterialTheme.typography.headlineSmall)
        Text(text = userEmail, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun SettingsItem(
    title: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = if (isDestructive) MaterialTheme.colorScheme.error else LocalContentColor.current,
            style = MaterialTheme.typography.bodyLarge
        )
        if (!isDestructive) {
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
} 