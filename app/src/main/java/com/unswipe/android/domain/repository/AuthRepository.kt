package com.unswipe.android.domain.repository

import com.google.firebase.auth.FirebaseUser // Import needed types
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    // Define the methods that the domain/UI layer will call
    suspend fun login(email: String, pass: String): Result<Unit>
    suspend fun register(email: String, pass: String): Result<Unit>
    suspend fun logout()
    suspend fun isUserPremium(): Boolean
    fun getCurrentUserFlow(): Flow<FirebaseUser?>
    fun getUserId(): String?
}