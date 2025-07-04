// Location: app/src/main/java/com/unswipe/android/data/repository/AuthRepositoryImpl.kt

package com.unswipe.android.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.unswipe.android.di.IoDispatcher // Assuming you have this Qualifier
import com.unswipe.android.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
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

    override suspend fun isUserPremium(): Boolean {
        TODO("Not yet implemented")
    }

    // Note: Implementing a robust Flow for auth state changes requires
    // using FirebaseAuth.addAuthStateListener and callbackFlow.
    // This is a simplified placeholder.
    override fun getCurrentUserFlow(): Flow<FirebaseUser?> {
        // TODO: Implement properly using callbackFlow and AuthStateListener
        // For now, returning a simple flow based on current state
        return kotlinx.coroutines.flow.flowOf(firebaseAuth.currentUser)
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