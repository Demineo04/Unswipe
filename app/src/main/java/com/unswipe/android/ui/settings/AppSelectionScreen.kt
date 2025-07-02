package com.unswipe.android.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import android.graphics.drawable.Drawable

// A simple data class to represent an app for the UI
data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: Drawable,
    var isBlocked: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSelectionScreen(
    // viewModel: AppSelectionViewModel = hiltViewModel() // TEMPORARILY DISABLED
) {
    // val appList by viewModel.apps.collectAsState() // TEMPORARILY DISABLED
    // val onAppBlockToggled: (String, Boolean) -> Unit = viewModel::setAppBlocked // TEMPORARILY DISABLED
    
    // TEMPORARILY SIMPLIFIED
    Text("App Selection Screen - Working!")
    
    /*
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
                        appInfo = AppInfo(
                            name = app.name,
                            packageName = app.packageName,
                            icon = app.icon,
                            isBlocked = app.isBlocked
                        ),
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
    val icon = appInfo.icon

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
    */
} 