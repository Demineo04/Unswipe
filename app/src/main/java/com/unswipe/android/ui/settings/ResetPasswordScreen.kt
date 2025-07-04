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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unswipe.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reset Password", color = UnswipeTextPrimary) },
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
                
                // Icon
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = UnswipePrimary,
                    modifier = Modifier.size(80.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Title
                Text(
                    text = "Reset Your Password",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = UnswipeTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
                
                Text(
                    text = "Enter your email address and we'll send you a link to reset your password.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = UnswipeTextSecondary,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Email field
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { viewModel.updateEmail(it) },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
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
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Send button
                Button(
                    onClick = { viewModel.sendResetEmail() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading && uiState.email.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UnswipePrimary,
                        disabledContainerColor = UnswipeGray
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = UnswipeBlack,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Send Reset Email",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                
                // Success message
                if (uiState.isSuccess) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = UnswipeGreen.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = UnswipeGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Reset email sent! Check your inbox.",
                                color = UnswipeGreen,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
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
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = UnswipeRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = error,
                                color = UnswipeRed,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

// ViewModel
data class ResetPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class ResetPasswordViewModel @javax.inject.Inject constructor(
    private val authRepository: com.unswipe.android.domain.repository.AuthRepository
) : androidx.lifecycle.ViewModel() {
    
    private val _uiState = kotlinx.coroutines.flow.MutableStateFlow(ResetPasswordUiState())
    val uiState: kotlinx.coroutines.flow.StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()
    
    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            error = null,
            isSuccess = false
        )
    }
    
    fun sendResetEmail() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                // TODO: Implement password reset in AuthRepository
                // authRepository.sendPasswordResetEmail(_uiState.value.email)
                
                // Simulate success for now
                kotlinx.coroutines.delay(1500)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to send reset email"
                )
            }
        }
    }
}