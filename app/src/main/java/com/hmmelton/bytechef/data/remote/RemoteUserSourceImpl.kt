package com.hmmelton.bytechef.data.remote

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.hmmelton.bytechef.data.auth.AuthManager
import com.hmmelton.bytechef.data.model.remote.RemoteUser
import kotlinx.coroutines.tasks.await

private const val TAG = "UserAuthDataSource"

/**
 * This class is used to interact with the remote Firebase Firestore data source to track/manage
 * data related to the user - authentication status and user info. The remote user data class used
 * is [RemoteUser].
 */
class RemoteUserSourceImpl(
    private val reference: CollectionReference
) : RemoteUserSource {

    /**
     * Create user info object for newly-registered user.
     */
    override suspend fun createUserData(
        authInfo: AuthManager.AuthInfo,
        displayName: String,
        dietaryRestrictions: List<String>,
        favoriteCuisines: List<String>
    ): RemoteUser? {
        return try {
            val remoteUser = RemoteUser(
                uid = authInfo.uid,
                email = authInfo.email.orEmpty(),
                displayName = displayName,
                dietaryRestrictions = dietaryRestrictions,
                favoriteCuisines = favoriteCuisines
            )
            reference.document(authInfo.uid).set(remoteUser).await()
            remoteUser
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create User object", e)
            null
        }
    }

    /**
     * Update a user's data with 1 or more of the provided arguments.
     */
    override suspend fun updateUserData(
        uid: String,
        favoriteRecipesIds: List<String>?,
        dietaryRestrictions: List<String>?,
        favoriteCuisines: List<String>?
    ): Boolean {
        // Create update map from only non-null arguments
        val updates = mutableMapOf<String, Any>()
        favoriteRecipesIds?.let { updates["favorite_recipe_ids"] = it }
        dietaryRestrictions?.let { updates["dietary_restrictions"] = it }
        favoriteCuisines?.let { updates["favorite_cuisines"] = it }

        // If all arguments were null, just return
        if (updates.isEmpty()) return true

        return try {
            reference.document(uid).update(updates).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update user data", e)
            false
        }
    }

    /**
     * Fetch data for user with provided uid.
     */
    override suspend fun fetchUserData(uid: String): RemoteUser? {
        return try {
            // Read user data from remote database, then cast it into local user data class. If
            // either of these steps fails, throw an exception
            val documentSnapshot = reference.document(uid).get().await()
            val user = documentSnapshot.toObject(RemoteUser::class.java)
                ?: throw ClassCastException()
            user
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch user data", e)
            null
        }
    }

    /**
     * Delete user data with provided uid.
     */
    override suspend fun deleteUserData(uid: String): Boolean {
        return try {
            // Attempt to delete user
            reference.document(uid).delete().await()

            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete user data", e)
            false
        }
    }
}
