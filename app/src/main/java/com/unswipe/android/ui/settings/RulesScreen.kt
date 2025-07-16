package com.unswipe.android.ui.settings

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unswipe.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesScreen(
    onNavigateBack: () -> Unit
) {
    var showAddRuleDialog by remember { mutableStateOf(false) }
    var rules by remember { mutableStateOf(mockRules) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MinimalistWhite)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(40.dp))
                
                // Header with back button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MinimalistBlack,
                                shape = RoundedCornerShape(0.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MinimalistWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Text(
                        text = "Rules",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Light,
                            color = MinimalistBlack
                        )
                    )
                    
                    // Placeholder for symmetry
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }
            
            item {
                // Description
                Text(
                    text = "Set time-based rules to block apps during specific hours",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MinimalistBlack,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
            
            item {
                // Add Rule Button
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
                        TextButton(
                            onClick = { showAddRuleDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = MinimalistBlack,
                                    modifier = Modifier.size(20.dp)
                                )
                                
                                Text(
                                    text = "Add New Rule",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = MinimalistBlack,
                                        fontSize = 16.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
            
            // Rules list
            items(rules) { rule ->
                RuleCard(
                    rule = rule,
                    onToggle = { ruleId ->
                        rules = rules.map { 
                            if (it.id == ruleId) it.copy(isEnabled = !it.isEnabled) else it 
                        }
                    },
                    onDelete = { ruleId ->
                        rules = rules.filter { it.id != ruleId }
                    }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(100.dp)) // Bottom padding
            }
        }
        
        // Add Rule Dialog
        if (showAddRuleDialog) {
            AddRuleDialog(
                onDismiss = { showAddRuleDialog = false },
                onRuleCreated = { newRule ->
                    rules = rules + newRule
                    showAddRuleDialog = false
                }
            )
        }
    }
}

@Composable
private fun RuleCard(
    rule: AppRule,
    onToggle: (String) -> Unit = {},
    onDelete: (String) -> Unit = {}
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = rule.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Normal,
                            color = MinimalistBlack,
                            fontSize = 18.sp
                        )
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Switch(
                            checked = rule.isEnabled,
                            onCheckedChange = { onToggle(rule.id) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MinimalistWhite,
                                checkedTrackColor = MinimalistBlack,
                                uncheckedThumbColor = MinimalistBlack,
                                uncheckedTrackColor = MinimalistWhite
                            )
                        )
                        
                        IconButton(
                            onClick = { onDelete(rule.id) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete rule",
                                tint = MinimalistBlack,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                
                // Time and apps info
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Time: ${rule.startTime} - ${rule.endTime}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MinimalistBlack,
                            fontSize = 14.sp
                        )
                    )
                    
                    Text(
                        text = "Apps: ${rule.blockedApps.joinToString(", ")}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MinimalistBlack,
                            fontSize = 14.sp
                        )
                    )
                    
                    Text(
                        text = "Days: ${rule.activeDays.joinToString(", ")}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MinimalistBlack,
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }
}

data class AppRule(
    val id: String,
    val name: String,
    val startTime: String,
    val endTime: String,
    val blockedApps: List<String>,
    val activeDays: List<String>,
    val isEnabled: Boolean
)

@Composable
private fun AddRuleDialog(
    onDismiss: () -> Unit,
    onRuleCreated: (AppRule) -> Unit
) {
    var ruleName by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("9:00 AM") }
    var endTime by remember { mutableStateOf("5:00 PM") }
    var selectedApps by remember { mutableStateOf(setOf<String>()) }
    var selectedDays by remember { mutableStateOf(setOf<String>()) }
    
    val availableApps = listOf("Discord", "Instagram", "TikTok", "YouTube", "Facebook", "Twitter", "Snapchat")
    val availableDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add New Rule",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MinimalistBlack,
                    fontWeight = FontWeight.Normal
                )
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = ruleName,
                        onValueChange = { ruleName = it },
                        label = { Text("Rule Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MinimalistBlack,
                            unfocusedBorderColor = MinimalistBlack.copy(alpha = 0.5f)
                        )
                    )
                }
                
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Time Range",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MinimalistBlack
                            )
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = startTime,
                                onValueChange = { startTime = it },
                                label = { Text("Start") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MinimalistBlack,
                                    unfocusedBorderColor = MinimalistBlack.copy(alpha = 0.5f)
                                )
                            )
                            OutlinedTextField(
                                value = endTime,
                                onValueChange = { endTime = it },
                                label = { Text("End") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MinimalistBlack,
                                    unfocusedBorderColor = MinimalistBlack.copy(alpha = 0.5f)
                                )
                            )
                        }
                    }
                }
                
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Select Apps to Block",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MinimalistBlack
                            )
                        )
                        availableApps.forEach { app ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Checkbox(
                                    checked = selectedApps.contains(app),
                                    onCheckedChange = { isChecked ->
                                        selectedApps = if (isChecked) {
                                            selectedApps + app
                                        } else {
                                            selectedApps - app
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MinimalistBlack,
                                        uncheckedColor = MinimalistBlack.copy(alpha = 0.5f)
                                    )
                                )
                                Text(
                                    text = app,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MinimalistBlack
                                    )
                                )
                            }
                        }
                    }
                }
                
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Select Days",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MinimalistBlack
                            )
                        )
                        availableDays.forEach { day ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Checkbox(
                                    checked = selectedDays.contains(day),
                                    onCheckedChange = { isChecked ->
                                        selectedDays = if (isChecked) {
                                            selectedDays + day
                                        } else {
                                            selectedDays - day
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MinimalistBlack,
                                        uncheckedColor = MinimalistBlack.copy(alpha = 0.5f)
                                    )
                                )
                                Text(
                                    text = day,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MinimalistBlack
                                    )
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (ruleName.isNotBlank() && selectedApps.isNotEmpty() && selectedDays.isNotEmpty()) {
                        val newRule = AppRule(
                            id = System.currentTimeMillis().toString(),
                            name = ruleName,
                            startTime = startTime,
                            endTime = endTime,
                            blockedApps = selectedApps.toList(),
                            activeDays = selectedDays.toList(),
                            isEnabled = true
                        )
                        onRuleCreated(newRule)
                    }
                }
            ) {
                Text(
                    text = "Create",
                    color = MinimalistBlack
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = MinimalistBlack
                )
            }
        }
    )
}

private val mockRules = listOf(
    AppRule(
        id = "1",
        name = "Work Focus",
        startTime = "9:00 AM",
        endTime = "5:00 PM",
        blockedApps = listOf("Instagram", "TikTok", "YouTube"),
        activeDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri"),
        isEnabled = true
    ),
    AppRule(
        id = "2",
        name = "Sleep Time",
        startTime = "10:00 PM",
        endTime = "7:00 AM",
        blockedApps = listOf("All Social Media"),
        activeDays = listOf("Every Day"),
        isEnabled = false
    ),
    AppRule(
        id = "3",
        name = "Study Time",
        startTime = "7:00 PM",
        endTime = "9:00 PM",
        blockedApps = listOf("Facebook", "Twitter", "Snapchat"),
        activeDays = listOf("Mon", "Wed", "Fri"),
        isEnabled = true
    )
)