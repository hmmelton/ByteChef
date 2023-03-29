package com.hmmelton.bytechef.data.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.hmmelton.bytechef.data.model.remote.RemoteUser
import kotlinx.coroutines.tasks.await

private const val TAG = "UserAuthDataSource"

/**
 * This class is used to interact with the remote Firebase Firestore data source to track/manage
 * data related to the user - authentication status and user info. The remote user data class used
 * is [RemoteUser].
 */
class UserAuthDataSource(
    private val auth: FirebaseAuth,
    private val reference: CollectionReference
) {
    /**
     * Check whether or not the user is currently authenticated
     */
    fun isAuthenticated() = auth.currentUser != null

    /**
     * Register a new user
     *
     * @param email user's account email
     * @param password user's accoutn password
     * @param dietaryRestrictions any dietary restrictions the user may have
     * @param favoriteCuisines user's favorite cuisines
     */
    suspend fun registerUser(
        email: String,
        password: String,
        dietaryRestrictions: List<String>,
        favoriteCuisines: List<String>
    ): Result<RemoteUser> {
        return try {
            val firebaseUser = auth.createUserWithEmailAndPassword(email, password).await().user
            val createUserResult = createUserInFirestore(firebaseUser!!, dietaryRestrictions, favoriteCuisines)

            // If Firestore failed to create the user object, we need to thrown an error and undo
            // the new user registration
            if (createUserResult.isFailure) {
                throw Exception("Failed to create user")
            }

            // Otherwise, return the result
            createUserResult
        } catch (e: Exception) {
            // Rollback by deleting the user from FirebaseAuth
            auth.currentUser?.delete()?.await()

            Result.failure(e)
        }
    }


    /**
     * Log in an existing user
     */
    suspend fun loginUser(email: String, password: String): FirebaseUser? {
        return try {
            auth.signInWithEmailAndPassword(email, password).await().user
        } catch (e: Exception) {
            Log.e(TAG, "error logging in user", e)
            null
        }
    }

    /**
     * Create user info object for newly-registered user
     *
     * @param user newly-created user
     * @param dietaryRestrictions user's dietary restrictions
     * @param favoriteCuisines user's favorite cuisines
     */
    private suspend fun createUserInFirestore(
        user: FirebaseUser,
        dietaryRestrictions: List<String>,
        favoriteCuisines: List<String>
    ): Result<RemoteUser> {
        return try {
            val remoteUser = RemoteUser(
                uid = user.uid,
                email = user.email.orEmpty(),
                dietaryRestrictions = dietaryRestrictions,
                favoriteCuisines = favoriteCuisines
            )
            reference.document(user.uid).set(remoteUser).await()
            Result.success(remoteUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update a user's data
     */
    suspend fun updateUserData(
        uid: String,
        favoriteRecipes: List<String>? = null,
        dietaryRestrictions: List<String>? = null,
        favoriteCuisines: List<String>? = null
    ) {
        val updates = mutableMapOf<String, Any>()
        favoriteRecipes?.let { updates["favorite_recipe_ids"] = it }
        dietaryRestrictions?.let { updates["dietary_restrictions"] = it }
        favoriteCuisines?.let { updates["favorite_cuisines"] = it }

        reference.document(uid).update(updates).await()
    }

    /**
     * Fetch data for user with provided uid
     */
    suspend fun fetchUserData(uid: String): RemoteUser? {
        return try {
            val documentSnapshot = reference.document(uid).get().await()
            documentSnapshot.toObject(RemoteUser::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
