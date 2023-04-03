package com.hmmelton.bytechef.data.repositories

import android.util.Log
import com.hmmelton.bytechef.data.local.RecipeDao
import com.hmmelton.bytechef.data.model.local.toRecipe
import com.hmmelton.bytechef.data.model.ui.Recipe
import com.hmmelton.bytechef.data.model.ui.toLocalRecipe
import com.hmmelton.bytechef.data.model.ui.toRemoteRecipe
import com.hmmelton.bytechef.data.remote.RemoteRecipeSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

private const val TAG = "RecipeRepositoryImpl"

/**
 * Implementation of [RecipeRepository].
 */
class RecipeRepositoryImpl(
    private val remoteRecipeSource: RemoteRecipeSource,
    private val recipeDao: RecipeDao
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
    override suspend fun forceRefreshRecipes(uid: String): Boolean {
        TODO("NOt yet implemented")
    }

    /**
     * ID of sync job, referenced to cancel the job in [stopSync].
     */
    private var syncJobId: UUID? = null

    /**
     * Start syncing data between remote and local data sources.
     */
    override suspend fun startSync() {
        TODO("Not yet implemented")
    }

    /**
     * Stop syncing data between remote and local data sources.
     */
    override suspend fun stopSync() {
        TODO("Not yet implemented")
    }
}
