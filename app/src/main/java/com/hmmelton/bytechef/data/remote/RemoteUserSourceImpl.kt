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
class RemoteUserSourceImpl(
    private val auth: FirebaseAuth,
    private val reference: CollectionReference
) : RemoteUserSource {
    /**
     * Check whether or not the user is currently authenticated
     */
    override fun isAuthenticated() = auth.currentUser != null

    /**
     * Register a new user
     *
     * @param email user's account email
     * @param password user's accoutn password
     * @param dietaryRestrictions any dietary restrictions the user may have
     * @param favoriteCuisines user's favorite cuisines
     */
    override suspend fun registerUser(
        email: String,
        password: String,
        dietaryRestrictions: List<String>,
        favoriteCuisines: List<String>
    ): RemoteUser? {
        return try {
            // Attempt to register the user, then create a user data object for them. If either of
            // these steps fails, throw an exception
            val firebaseUser = auth.createUserWithEmailAndPassword(email, password).await().user
                ?: throw Exception("Failed to register user")
            val user =
                createUserInFirestore(firebaseUser, dietaryRestrictions, favoriteCuisines)
                    ?: throw Exception("Failed to create User object")

            // Otherwise, return user data
            user
        } catch (e: Exception) {
            // Rollback by deleting the user from FirebaseAuth
            auth.currentUser?.delete()?.await()
            Log.e(TAG, "failed to register user", e)

            null
        }
    }


    /**
     * Log in an existing user with provided credentials
     */
    override suspend fun loginUser(email: String, password: String): RemoteUser? {
        return try {
            // Attempt to log in the user, then fetch their corresponding user data. If either of
            // these steps fails, throw an exception
            val firebaseUser = auth.signInWithEmailAndPassword(email, password).await().user
                ?: throw Exception("Failed to log in user")
            val user = fetchUserData(firebaseUser.uid)
                ?: throw Exception("Failed to fetch user data")

            // Otherwise return user data
            user
        } catch (e: Exception) {
            Log.e(TAG, "failed to log in ", e)
            null
        }
    }

    /**
     * Update a user's data with 1 or more of the provided arguments.
     */
    override suspend fun updateUserData(
        uid: String,
        favoriteRecipes: List<String>?,
        dietaryRestrictions: List<String>?,
        favoriteCuisines: List<String>?
    ): Boolean {
        val updates = mutableMapOf<String, Any>()
        favoriteRecipes?.let { updates["favorite_recipe_ids"] = it }
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
     * Fetch data for user with provided uid
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
    ): RemoteUser? {
        return try {
            val remoteUser = RemoteUser(
                uid = user.uid,
                email = user.email.orEmpty(),
                dietaryRestrictions = dietaryRestrictions,
                favoriteCuisines = favoriteCuisines
            )
            reference.document(user.uid).set(remoteUser).await()
            remoteUser
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create User object", e)
            null
        }
    }
}
