package com.hmmelton.bytechef.data.local

import User
import android.util.Log
import androidx.datastore.core.DataStore
import com.hmmelton.bytechef.data.model.remote.RemoteUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private const val TAG = "UserStore"

class UserStore(private val dataStore: DataStore<User>) {
    /**
     * Read user data. If an error occurs, emit the default (empty) data for [User]
     */
    val user: Flow<User> = dataStore.data
        .catch { exception ->
            Log.e(TAG, "Exception emitting User data", exception)
            emit(User.getDefaultInstance())
        }

    /**
     * Sync local data with remote data source.
     */
    suspend fun syncWithRemote(remoteUser: RemoteUser): User? {
        return try {
            // Attempt to update locally stored data with the remote values
            dataStore.updateData { current ->
                current.toBuilder()
                    .setId(remoteUser.uid)
                    .setDisplayName(remoteUser.displayName)
                    .setEmail(remoteUser.email)
                    .setList(remoteUser.favoriteRecipeIds, User.Builder::setFavoriteRecipeIds)
                    .setList(remoteUser.dietaryRestrictions, User.Builder::setDietaryRestrictions)
                    .setList(remoteUser.favoriteCuisines, User.Builder::setFavoriteCuisines)
                    .build()
            }
        } catch (e: Exception) {
            // Return null if the update fails
            Log.e(TAG, "Failed to sync with remote user", e)
            null
        }
    }

    /**
     * Update user data. This function is triggered by a direct request from the user.
     */
    suspend fun updateUser(
        favoriteRecipeIds: List<String>? = null,
        dietaryRestrictions: List<String>? = null,
        favoriteCuisines: List<String>? = null
    ): User? {
        return try {
            // Attempt to update local storage with the new (non-null) values
            dataStore.updateData { current ->
                val builder = current.toBuilder()
                favoriteRecipeIds?.let {
                    builder.setList(it, User.Builder::setFavoriteRecipeIds)
                }
                dietaryRestrictions?.let {
                    builder.setList(it, User.Builder::setDietaryRestrictions)
                }
                favoriteCuisines?.let {
                    builder.setList(it, User.Builder::setFavoriteCuisines)
                }
                builder.build()
            }
        } catch (e: Exception) {
            // Return null if the update fails
            Log.e(TAG, "Failed to update user")
            null
        }
    }

    /**
     * Clear the current [User] data from local storage
     */
    suspend fun clearUser() {
        dataStore.updateData { User.getDefaultInstance() }
    }
}

/**
 * Extension function for updating a List property when writing to the [User] [DataStore].
 */
fun User.Builder.setList(
    values: List<String>,
    function: User.Builder.(Int, String) -> User.Builder
): User.Builder {
    values.forEachIndexed { index, s ->
        function(index, s)
    }
    return this
}
