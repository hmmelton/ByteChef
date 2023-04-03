package com.hmmelton.bytechef.data.repositories

import User
import android.util.Log
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import com.hmmelton.bytechef.data.auth.AuthManager
import com.hmmelton.bytechef.data.local.UserStore
import com.hmmelton.bytechef.data.remote.RemoteUserSource
import com.hmmelton.bytechef.data.workers.SynchronizeUserDataWorker
import com.hmmelton.bytechef.data.workers.WorkKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import java.util.concurrent.TimeUnit

private const val TAG = "UserRepositoryImpl"

/**
 * Implementation of [UserRepository].
 */
class UserRepositoryImpl(
    private val remoteUserSource: RemoteUserSource,
    private val userStore: UserStore,
    private val workManager: WorkManager
) : UserRepository, SynchronizedRepository {

    /**
     * Create data for new user.
     */
    override suspend fun createUserData(
        authInfo: AuthManager.AuthInfo,
        displayName: String,
        dietaryRestrictions: List<String>,
        favoriteCuisines: List<String>
    ): Boolean {
        return try {
            // Create user in remote source, throwing exception in case of failure
            val remoteUser = remoteUserSource.createUserData(
                authInfo,
                displayName,
                dietaryRestrictions,
                favoriteCuisines
            ) ?: throw Exception("Failed to create user data in remote source")

            // Create user in local source, throwing exception in case of failure
            val user = userStore.syncWithRemote(remoteUser)

            // If saving the user locally failed, undo remote object creation
            if (user == null) {
                remoteUserSource.deleteUserData(authInfo.uid)
                throw Exception("Failed to create user data in local source")
            }

            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create user data", e)
            false
        }
    }

    /**
     * Update user's favorites recipes, dietary restrictions, and favorite cuisines.
     */
    override suspend fun updateUserData(
        uid: String,
        favoriteRecipeIds: List<String>?,
        dietaryRestrictions: List<String>?,
        favoriteCuisines: List<String>?
    ): Boolean {
        return try {
            val user = userStore.user.first()
            if (user.id != uid || user.id.isEmpty()) {
                // User not found in local store
                Log.e(TAG, "Error fetching user from local source")
                return false
            }

            // Update remote user data
            val updatedRemoteSource = remoteUserSource.updateUserData(
                uid = user.id,
                favoriteRecipesIds = favoriteRecipeIds,
                dietaryRestrictions = dietaryRestrictions,
                favoriteCuisines = favoriteCuisines
            )

            if (!updatedRemoteSource) return false

            // Update local user data
            userStore.updateUser(favoriteRecipeIds, dietaryRestrictions, favoriteCuisines)
                ?: throw Exception("Failed to update user data in local source")

            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update user data", e)
            false
        }
    }

    /**
     * Function that returns user data as a [Flow]. A null [User] means there is currently no
     * authenticated user.
     */
    override suspend fun observeUser(): Flow<User?> = userStore.user
        .map {
            if (it.id.isEmpty()) null
            else it
        }

    /**
     * Function that forces a data refresh from the remote data source.
     *
     * @param uid ID of current user
     *
     * @return whether or not the refresh/sync succeeded
     */
    override suspend fun forceRefreshUser(uid: String): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Delete user info with provided uid.
     */
    override suspend fun deleteUserData(uid: String, localSourceOnly: Boolean): Boolean {
        return try {
            // Delete data locally
            userStore.clearUser()

            // Only if it is specified this was not a local-only request, attempt to delete data
            // in remote data source
            if (!localSourceOnly) {
                val result = remoteUserSource.deleteUserData(uid)
                if (!result) throw Exception("Failed to delete data in remote source")
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete user data", e)
            false
        }
    }

    /**
     * ID of sync job, referenced to cancel the job in [stopSync].
     */
    private var syncJobId: UUID? = null

    /**
     * Schedule data synchronization.
     */
    override suspend fun startSync() {
        try {
            // Fetch current user ID
            val user = userStore.user.first()

            // Constrain job to run only when there is a network connection
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            // Custom input data containing current user's ID
            val inputData = Data.Builder()
                .putString(WorkKeys.UID, user.id)
                .build()

            // Request job to run every 1 hour with 15min flex time. Add previously-defined
            // constraints and input data.
            val workRequest = PeriodicWorkRequestBuilder<SynchronizeUserDataWorker>(
                1, TimeUnit.HOURS, // Set the repeat interval to 1 hour.
                15, TimeUnit.MINUTES // Set the flex interval to 15 minutes.
            )
                .setConstraints(constraints)
                .setInputData(inputData)
                .build()

            // Enqueue job
            workManager.enqueueUniquePeriodicWork(
                "SynchronizeUserData",
                ExistingPeriodicWorkPolicy.KEEP, // Use KEEP or REPLACE based on your desired behavior.
                workRequest
            ).await()

            // If job was enqueued, update global job ID
            syncJobId = workRequest.id
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start data sync", e)
        }
    }

    /**
     * Cancel existing data synchronization.
     */
    override suspend fun stopSync() {
        syncJobId?.let { workManager.cancelWorkById(it) }
    }
}

