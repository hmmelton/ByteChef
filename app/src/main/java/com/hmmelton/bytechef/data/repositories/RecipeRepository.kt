package com.hmmelton.bytechef.data.repositories

import com.hmmelton.bytechef.data.model.ui.Recipe
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining a repository for interacting with recipe data.
 */
interface RecipeRepository {

    /**
     * Flow for reading recipe data.
     */
    suspend fun observeRecipes(): Flow<List<Recipe>>

    /**
     * Create a new recipe data object.
     *
     * @param recipe recipe to be saved
     *
     * @return whether or not the object creation was successful
     */
    suspend fun createRecipe(recipe: Recipe): Boolean

    /**
     * Update an existing recipe data object.
     *
     * @param recipe recipe to be updated
     *
     * @return whether or not the update was successful
     */
    suspend fun updateRecipe(recipe: Recipe): Boolean

    /**
     * Delete a recipe data object.
     *
     * @param recipeId id of recipe to be deleted
     *
     * @return whether or not the deletion was successful
     */
    suspend fun deleteRecipe(recipeId: String): Boolean
}
