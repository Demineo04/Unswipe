package com.unswipe.android.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unswipe.android.ui.theme.*
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", color = UnswipeTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = UnswipeTextPrimary)
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.saveProfile()
                            onNavigateBack()
                        }
                    ) {
                        Text("Save", color = UnswipePrimary)
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
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Name field
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = { viewModel.updateName(it) },
                    label = { Text("Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = UnswipePrimary,
                        unfocusedBorderColor = UnswipeGray,
                        focusedLabelColor = UnswipePrimary,
                        unfocusedLabelColor = UnswipeTextSecondary,
                        cursorColor = UnswipePrimary,
                        focusedTextColor = UnswipeTextPrimary,
                        unfocusedTextColor = UnswipeTextPrimary
                    )
                )
                
                // Email field (read-only)
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = UnswipeGray.copy(alpha = 0.5f),
                        disabledLabelColor = UnswipeTextSecondary.copy(alpha = 0.5f),
                        disabledTextColor = UnswipeTextSecondary
                    )
                )
                
                // Bio field
                OutlinedTextField(
                    value = uiState.bio,
                    onValueChange = { viewModel.updateBio(it) },
                    label = { Text("Bio") },
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = UnswipePrimary,
                        unfocusedBorderColor = UnswipeGray,
                        focusedLabelColor = UnswipePrimary,
                        unfocusedLabelColor = UnswipeTextSecondary,
                        cursorColor = UnswipePrimary,
                        focusedTextColor = UnswipeTextPrimary,
                        unfocusedTextColor = UnswipeTextPrimary
                    )
                )
                
                // Loading indicator
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = UnswipePrimary)
                    }
                }
                
                // Error message
                uiState.error?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = UnswipeRed.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = error,
                            color = UnswipeRed,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// ViewModel
data class EditProfileUiState(
    val name: String = "",
    val email: String = "",
    val bio: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class EditProfileViewModel @javax.inject.Inject constructor(
    private val authRepository: com.unswipe.android.domain.repository.AuthRepository,
    private val settingsRepository: com.unswipe.android.domain.repository.SettingsRepository
) : androidx.lifecycle.ViewModel() {
    
    private val _uiState = kotlinx.coroutines.flow.MutableStateFlow(EditProfileUiState())
    val uiState: kotlinx.coroutines.flow.StateFlow<EditProfileUiState> = _uiState.asStateFlow()
    
    init {
        loadProfile()
    }
    
    private fun loadProfile() {
        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser()
                _uiState.value = _uiState.value.copy(
                    name = user?.displayName ?: "",
                    email = user?.email ?: "",
                    bio = "" // TODO: Load from user preferences
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load profile"
                )
            }
        }
    }
    
    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }
    
    fun updateBio(bio: String) {
        _uiState.value = _uiState.value.copy(bio = bio)
    }
    
    fun saveProfile() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                // TODO: Implement profile update in repository
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to save profile"
                )
            }
        }
    }
}