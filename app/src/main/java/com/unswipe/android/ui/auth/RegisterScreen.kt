// Location: app/src/main/java/com/unswipe/android/ui/auth/RegisterScreen.kt

package com.unswipe.android.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel, // Get from NavGraph
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit // Callback to trigger navigation
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") } // Added confirm password
    val authState by viewModel.authState.collectAsState()

    // Navigate away automatically if registration leads to authenticated state
    LaunchedEffect(authState) {
        if (authState is AuthViewModel.AuthState.Authenticated) {
            onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Register for Unswipe", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField( // Confirm password field
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            isError = password != confirmPassword && confirmPassword.isNotEmpty() // Basic validation
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Handle loading and error states
        when (val state = authState) {
            is AuthViewModel.AuthState.Loading -> {
                CircularProgressIndicator()
            }
            is AuthViewModel.AuthState.Error -> {
                Text(state.message, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }
            else -> { // Authenticated (handled by LaunchedEffect) or Unauthenticated
                Button(
                    onClick = { viewModel.onEvent(AuthViewModel.AuthEvent.Register(email, password)) },
                    modifier = Modifier.fillMaxWidth(),
                    // Basic validation including password match
                    enabled = email.isNotBlank() && password.isNotBlank() && password == confirmPassword
                ) {
                    Text("Register")
                }
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text("Already have an account? Login")
        }
    }
}