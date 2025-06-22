package com.unswipe.android.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter

// A simple data class to represent an app for the UI
data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: Painter,
    var isBlocked: Boolean
)

@Composable
fun AppSelectionScreen(
    viewModel: AppSelectionViewModel = hiltViewModel()
) {
    val appList by viewModel.apps.collectAsState()
    val onAppBlockToggled: (String, Boolean) -> Unit = viewModel::setAppBlocked
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("App Blocker") })
        }
    ) { padding ->
        if (appList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                items(appList) { app ->
                    AppListItem(
                        appInfo = app,
                        onBlockToggled = { isBlocked ->
                            onAppBlockToggled(app.packageName, isBlocked)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppListItem(
    appInfo: AppInfo,
    onBlockToggled: (Boolean) -> Unit
) {
    val packageManager = LocalContext.current.packageManager
    val icon = remember(appInfo.packageName) {
        appInfo.icon.loadIcon(packageManager)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberDrawablePainter(drawable = icon),
                contentDescription = "${appInfo.name} icon",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(appInfo.name, style = MaterialTheme.typography.bodyLarge)
        }
        Switch(
            checked = appInfo.isBlocked,
            onCheckedChange = onBlockToggled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary
            )
        )
    }
} 