// Location: app/src/main/java/com/unswipe/android/ui/auth/AuthViewModel.kt

package com.unswipe.android.ui.auth


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.unswipe.android.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // --- State Definitions ---
    sealed class AuthState {
        object Loading : AuthState()
        data class Authenticated(val user: FirebaseUser?) : AuthState() // Include user if needed later
        object Unauthenticated : AuthState()
        data class Error(val message: String) : AuthState()
    }

    sealed class AuthEvent {
        data class Login(val email: String, val pass: String) : AuthEvent()
        data class Register(val email: String, val pass: String) : AuthEvent()
        object Logout : AuthEvent()
        object ClearError : AuthEvent() // To dismiss error messages
    }

    // --- State Flows ---
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Observe Firebase Auth state changes from the repository
    init {
        viewModelScope.launch {
            // Use flatMapLatest or stateIn for better lifecycle management if needed
            authRepository.getCurrentUserFlow() // Assumes this flow emits updates
                .collect { user ->
                    if (user != null) {
                        _authState.value = AuthState.Authenticated(user)
                    } else {
                        _authState.value = AuthState.Unauthenticated
                    }
                }
        }
    }

    // --- Event Handling ---
    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.Login -> loginUser(event.email, event.pass)
            is AuthEvent.Register -> registerUser(event.email, event.pass)
            is AuthEvent.Logout -> logout()
            is AuthEvent.ClearError -> {
                // Clear error only if currently in error state
                if (_authState.value is AuthState.Error) {
                    // Re-check actual auth status after clearing error
                    val currentUser = authRepository.getUserId() != null // Quick check
                    _authState.value = if (currentUser) AuthState.Authenticated(null) else AuthState.Unauthenticated
                }
            }
        }
    }

    // --- Private Logic Functions ---
    private fun loginUser(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.login(email, pass)
            if (result.isSuccess) {
                // Auth state flow should update automatically via the collector above
                // _authState.value = AuthState.Authenticated(authRepository.getCurrentUser()) // Or rely on flow
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    private fun registerUser(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.register(email, pass)
            if (result.isSuccess) {
                // Auth state flow should update automatically via the collector above
                // _authState.value = AuthState.Authenticated(authRepository.getCurrentUser()) // Or rely on flow
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun logout() { // Make public if called directly from UI NavGraph
        viewModelScope.launch {
            authRepository.logout()
            // Auth state flow should update automatically via the collector above
            // _authState.value = AuthState.Unauthenticated // Or rely on flow
        }
    }
}



