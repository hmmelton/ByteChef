package com.hmmelton.bytechef.data.repositories

import android.util.Log
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import com.hmmelton.bytechef.data.local.RecipeDao
import com.hmmelton.bytechef.data.model.local.toRecipe
import com.hmmelton.bytechef.data.model.ui.Recipe
import com.hmmelton.bytechef.data.model.ui.toLocalRecipe
import com.hmmelton.bytechef.data.model.ui.toRemoteRecipe
import com.hmmelton.bytechef.data.remote.RemoteRecipeSource
import com.hmmelton.bytechef.data.workers.SynchronizeRecipeDataWorker
import com.hmmelton.bytechef.data.workers.WorkKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "RecipeRepositoryImpl"

/**
 * Implementation of [RecipeRepository].
 */
class RecipeRepositoryImpl @Inject constructor(
    private val remoteRecipeSource: RemoteRecipeSource,
    private val recipeDao: RecipeDao,
    private val userRepository: UserRepository,
    private val workManager: WorkManager
) : RecipeRepository, SynchronizedRepository {

    /**
     * Fetch a list of recipes created by the user.
     */
    override suspend fun observeRecipes(): Flow<List<Recipe>> =
        recipeDao.getAllRecipes()
            .map {
                if (it.isEmpty()) emptyList()
                else it.map { localRecipe ->  localRecipe.toRecipe() }
            }

    /**
     * Function to create a new recipe.
     *
     * @param recipe Recipe to be created
     *
     * @return result of creation attempt
     */
    override suspend fun createRecipe(recipe: Recipe): Boolean {
        return try {
            // Save first to remote source then local, throwing an exception if either fails
            val remoteResult = remoteRecipeSource.createRecipe(recipe.toRemoteRecipe())
            if (!remoteResult) throw Exception("Failed to create recipe in remote data source")

            val localResult = runCatching { recipeDao.insertFullRecipe(recipe.toLocalRecipe()) }

            // If we saved the recipe remotely, but cannot save it locally, roll back the remote
            // save
            if (localResult.isFailure) {
                remoteRecipeSource.deleteRecipe(recipe.id)
                false
            } else {
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add recipe", e)
            false
        }
    }

    /**
     * Update an existing recipe.
     *
     * @param recipe recipe with updated data
     *
     * @return whether or not the update succeeded
     */
    override suspend fun updateRecipe(recipe: Recipe): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Delete a recipe.
     *
     * @param recipeId ID of recipe object to be deleted
     *
     * @return whether or not the deletion was successful
     */
    override suspend fun deleteRecipe(recipeId: String): Boolean {
        return try {
            // Attempt to delete recipe locally, then remotely
            recipeDao.deleteRecipe(recipeId)
            remoteRecipeSource.deleteRecipe(recipeId)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete recipe", e)
            false
        }
    }

    /**
     * Function that forces a data refresh from the remote data source.
     *
     * @param uid user's UID.
     *
     * @return whether or not the refresh/sync succeeded
     */
    override suspend fun syncData(uid: String): Boolean {
        // TODO: sync data
        return true
    }

    /**
     * ID of sync job, referenced to cancel the job in [stopSync].
     */
    private var syncJobId: UUID? = null

    /**
     * Start syncing data between remote and local data sources.
     */
    override suspend fun startSync() {
        try {
            // Fetch current UID
            val uid = userRepository.observeUser().first()?.id ?: run {
                // Return early if UID is null
                Log.i(TAG, "Cannot start sync - uid is null")
                return
            }

            // Constrain job to run only when there is a network connection
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            // Custom input data containing current user's ID
            val inputData = Data.Builder()
                .putString(WorkKeys.UID, uid)
                .build()

            // Request job to run every 1 hour with 15min flex time. Add previously-defined
            // constraints and input data.
            val workRequest = PeriodicWorkRequestBuilder<SynchronizeRecipeDataWorker>(
                1, TimeUnit.HOURS, // Set the repeat interval to 1 hour.
                15, TimeUnit.MINUTES // Set the flex interval to 15 minutes.
            )
                .setConstraints(constraints)
                .setInputData(inputData)
                .build()

            // Enqueue job
            workManager.enqueueUniquePeriodicWork(
                "SynchronizeRecipeData",
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
     * Stop syncing data between remote and local data sources.
     */
    override suspend fun stopSync() {
        syncJobId?.let { workManager.cancelWorkById(it) }
    }
}
