package com.hmmelton.bytechef.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.dataStore
import com.hmmelton.bytechef.data.model.local.User
import com.hmmelton.bytechef.data.model.remote.RemoteUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

private const val TAG = "UserStore"

/**
 * Class used for persisting user info locally.
 */
class UserStore @Inject constructor(private val dataStore: DataStore<User>) {
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
     *
     * @param remoteUser data from remote source
     *
     * @return user saved to local source, or null if the sync failed
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
     *
     * @param favoriteRecipeIds list of IDs of user's favorite recipes
     * @param dietaryRestrictions list of user's dietary restrictitons
     * @param favoriteCuisines list of user's favorite cuisines
     *
     * @return updated user data, or null if update failed
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
     * Clear the current [User] data from local storage.
     */
    @Throws(IOException::class, Exception::class)
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

val Context.userDataStore: DataStore<User> by dataStore(
    fileName = "user.pb",
    serializer = UserSerializer
)
