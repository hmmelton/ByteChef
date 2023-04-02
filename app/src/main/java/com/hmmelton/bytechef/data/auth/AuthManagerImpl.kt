package com.hmmelton.bytechef.data.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

private const val TAG = "AuthManagerImpl"

/**
 * Implementation of [AuthManager] for managing user authentication state.
 */
class AuthManagerImpl(private val auth: FirebaseAuth) : AuthManager {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeAuthInfo() = auth.authInfoFlow()

    /**
     * Register a new user.
     */
    override suspend fun registerUser(
        email: String,
        password: String,
        dietaryRestrictions: List<String>,
        favoriteCuisines: List<String>
    ): AuthManager.AuthInfo? {
        return try {
            // Attempt to register the user, throwing an exception in case of failure
            val firebaseUser = auth.createUserWithEmailAndPassword(email, password).await().user
                ?: throw Exception()

            AuthManager.AuthInfo(firebaseUser.uid, firebaseUser.email)
        } catch (e: Exception) {
            // Return null if registration failed
            Log.e(TAG, "failed to register user", e)
            null
        }
    }

    /**
     * Login an existing user.
     */
    override suspend fun loginUser(email: String, password: String): AuthManager.AuthInfo? {
        return try {
            // Attempt to login user, throwing an exception in case of failure
            val firebaseUser = auth.signInWithEmailAndPassword(email, password).await().user
                ?: throw Exception()

            AuthManager.AuthInfo(firebaseUser.uid, firebaseUser.email)
        } catch (e: Exception) {
            // Return null if login failed
            Log.e(TAG, "Failed to login user", e)
            null
        }
    }
}

/**
 * Extension function to help with converting a [FirebaseAuth.AuthStateListener] to a Kotlin
 * coroutine [Flow] of [AuthManager.AuthInfo].
 */
@ExperimentalCoroutinesApi
fun FirebaseAuth.authInfoFlow(): Flow<AuthManager.AuthInfo?> = callbackFlow {
    val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val info = auth.currentUser?.let { AuthManager.AuthInfo(it.uid, it.email) }
        trySendBlocking(info)
    }

    addAuthStateListener(authStateListener)
    awaitClose { removeAuthStateListener(authStateListener) }
}