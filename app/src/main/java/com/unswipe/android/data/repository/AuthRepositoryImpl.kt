// Location: app/src/main/java/com/unswipe/android/data/repository/AuthRepositoryImpl.kt

package com.unswipe.android.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.unswipe.android.di.IoDispatcher // Assuming you have this Qualifier
import com.unswipe.android.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor( // <-- Hilt knows how to make this
    private val firebaseAuth: FirebaseAuth,   // <-- Hilt provides this from FirebaseModule
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher // <-- Hilt provides this from AppModule
) : AuthRepository { // <-- Implements the domain interface

    // --- Implement ALL methods defined in the AuthRepository interface ---
    // These are just placeholders - real implementation needed!

    override suspend fun login(email: String, pass: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            Result.success(Unit) // Indicate success
        } catch (e: Exception) {
            Result.failure(e) // Return the exception on failure
        }
    }

    override suspend fun register(email: String, pass: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() = withContext(ioDispatcher){
        try {
            firebaseAuth.signOut()
            // Maybe clear some local data if needed
        } catch (e: Exception) {
            // Log error? Usually signout doesn't fail critically
        }
    }

    override suspend fun isUserPremium(): Boolean = withContext(ioDispatcher) {
        try {
            // Check if user exists first
            val currentUser = firebaseAuth.currentUser ?: return@withContext false
            
            // Check custom claims for premium status
            val idTokenResult = currentUser.getIdToken(false).await()
            val isPremium = idTokenResult.claims["premium"] as? Boolean ?: false
            
            isPremium
        } catch (e: Exception) {
            // Log error and default to non-premium
            e.printStackTrace()
            false
        }
    }

    override fun getCurrentUserFlow(): Flow<FirebaseUser?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        
        firebaseAuth.addAuthStateListener(authStateListener)
        
        // Send current state immediately
        send(firebaseAuth.currentUser)
        
        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }

    override fun getUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }
    
    override suspend fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
    
    override suspend fun deleteAccount(): Result<Unit> = withContext(ioDispatcher) {
        try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                user.delete().await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("No user logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}