// Location: app/src/main/java/com/unswipe/android/ui/auth/LoginScreen.kt

package com.unswipe.android.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import com.unswipe.android.ui.auth.AuthViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.unswipe.android.ui.components.PrimaryButton
import com.unswipe.android.ui.components.UnswipeTextField
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthViewModel.AuthState.Authenticated) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main content area
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Welcome Back!",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                "Sign in to access your account",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp)
            )

            UnswipeTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            )
            Spacer(modifier = Modifier.height(16.dp))

            UnswipeTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onNavigateToForgotPassword,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Forgot Password?")
            }
            Spacer(modifier = Modifier.height(24.dp))

            when (val state = authState) {
                is AuthViewModel.AuthState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                else -> {
                    PrimaryButton(
                        text = "Login",
                        onClick = { viewModel.onEvent(AuthViewModel.AuthEvent.Login(email, password)) },
                        enabled = email.isNotBlank() && password.isNotBlank()
                    )
                }
            }
             if (authState is AuthViewModel.AuthState.Error) {
                Text(
                    (authState as AuthViewModel.AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Bottom navigation text
        TextButton(
            onClick = onNavigateToRegister,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 32.dp)
        ) {
            Text("New to Unswipe? Sign up")
        }
    }
}