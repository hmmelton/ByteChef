package com.hmmelton.bytechef.data.repositories

import User
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.hmmelton.bytechef.data.local.UserStore
import com.hmmelton.bytechef.data.model.remote.RemoteUser
import com.hmmelton.bytechef.data.remote.RemoteUserSource
import com.hmmelton.bytechef.data.workers.SynchronizeUserDataWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import java.util.concurrent.TimeUnit

private const val TAG = "UserRepositoryImpl"

/**
 * Implementation of [UserRepository].
 */
class UserRepositoryImpl(
    private val remoteUserSource: RemoteUserSource,
    private val userStore: UserStore,
    private val workManager: WorkManager
) : UserRepository {

    /**
     * Check if the user is authenticated.
     *
     * @return true if authenticated, false otherwise.
     */
    override fun isAuthenticated(): Flow<Boolean> {
        return remoteUserSource.getCurrentUid()
            .map { uid ->
                // If uid is null, clear user from local storage
                if (uid == null) userStore.clearUser()

                // User is authenticated if the uid is not null
                uid !=  null
            }
    }

    /**
     * Register a new user.
     *
     * @param email user's email.
     * @param password user's password.
     * @param dietaryRestrictions user's dietary restrictions.
     * @param favoriteCuisines user's favorite cuisines.
     *
     * @return created User if successful, null otherwise.
     */
    override suspend fun registerUser(
        email: String,
        password: String,
        dietaryRestrictions: List<String>,
        favoriteCuisines: List<String>
    ): User? {
        val remoteUser = remoteUserSource.registerUser(
            email, password, dietaryRestrictions, favoriteCuisines
        ) ?: return null

        return saveUserToLocalStorage(remoteUser)
    }

    /**
     * Log in an existing user.
     *
     * @param email user's email.
     * @param password user's password.
     *
     * @return User if successful, null otherwise.
     */
    override suspend fun loginUser(email: String, password: String): User? {
        val remoteUser = remoteUserSource.loginUser(email, password) ?: return null

        return saveUserToLocalStorage(remoteUser)
    }

    /**
     * Update user's favorites recipes, dietary restrictions, and favorite cuisines.
     *
     * @param uid user's UID.
     * @param favoriteRecipeIds list of user's favorite recipes.
     * @param dietaryRestrictions list of user's dietary restrictions.
     * @param favoriteCuisines list of user's favorite cuisines.
     */
    override suspend fun updateUserData(
        uid: String,
        favoriteRecipeIds: List<String>?,
        dietaryRestrictions: List<String>?,
        favoriteCuisines: List<String>?
    ): Boolean {
        val user = userStore.user.first()
        if (user.id != uid || user.id.isEmpty()) {
            // User not found in local store
            Log.e(TAG, "Error fetching user from local source")
            return false
        }

        // Update remote user data
        val updatedRemoteSource = remoteUserSource.updateUserData(
            uid = user.id,
            favoriteRecipes = favoriteRecipeIds,
            dietaryRestrictions = dietaryRestrictions,
            favoriteCuisines = favoriteCuisines
        )

        if (!updatedRemoteSource) return false

        // Update local user data
        userStore.updateUser(favoriteRecipeIds, dietaryRestrictions, favoriteCuisines)
            ?: return false

        return true
    }

    /**
     * Function that returns user data as a [Flow]. A null [User] means there is currently no
     * authenticated user.
     */
    override suspend fun getUser(): Flow<User?> = userStore.user
        .map {
            if (it.id.isEmpty()) null
            else it
        }.onStart {
            // When this Flow begins emitting data, start data source synchronization and do a
            // forced remote data refresh
            scheduleDataSynchronization()
            if (!forceRefreshUser()) {
                Log.e(TAG, "Failed to force refresh user data")
            }
        }
        .onCompletion { exception ->
            // When this Flow has completed (due to cancellation or failure), cancel the running
            // remote data synchronization job
            Log.i(TAG, "getUser Flow completed")
            exception?.let { Log.e(TAG, "Exception thrown from getUser Flow", exception) }
            cancelDataSynchronization()
        }

    /**
     * Function that forces a data refresh from the remote data source.
     *
     * @return whether or not the refresh/sync succeeded
     */
    override suspend fun forceRefreshUser(): Boolean {
        return try {
            //
            val currentUid = remoteUserSource.getCurrentUid().first()
                ?: throw Exception("Current UID null")
            val remoteUser = remoteUserSource.fetchUserData(currentUid)
                ?: throw Exception("Failed to fetch remote user data")
            userStore.syncWithRemote(remoteUser)
                ?: throw Exception("Failed to sync remote user data locally")

            // Refresh was a success
            true
        } catch (e: Exception) {
            // Refresh failed
            Log.e(TAG, "Failed to get user data", e)
            false
        }
    }

    /**
     * Save the RemoteUser to local storage.
     *
     * @param remoteUser RemoteUser instance.
     *
     * @return created User instance.
     */
    private suspend fun saveUserToLocalStorage(remoteUser: RemoteUser) =
        userStore.syncWithRemote(remoteUser)

    /**
     * [PeriodicWorkRequest] for syncing local data source with remote.
     */
    private val syncWorkRequest by lazy {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        PeriodicWorkRequestBuilder<SynchronizeUserDataWorker>(
            1, TimeUnit.HOURS, // Set the repeat interval. Here, it's set to 1 hour.
            15, TimeUnit.MINUTES // Set the flex interval. Here, it's set to 15 minutes.
        )
            .setConstraints(constraints)
            .build()
    }

    /**
     * Schedule data synchronization.
     */
    private fun scheduleDataSynchronization() {
        workManager.enqueueUniquePeriodicWork(
            "SynchronizeUserData",
            ExistingPeriodicWorkPolicy.KEEP, // Use KEEP or REPLACE based on your desired behavior.
            syncWorkRequest
        )
    }

    /**
     * Cancel existing data synchronization.
     */
    private fun cancelDataSynchronization() {
        workManager.cancelWorkById(syncWorkRequest.id)
    }
}

