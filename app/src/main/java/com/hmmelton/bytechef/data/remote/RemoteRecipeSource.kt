package com.hmmelton.bytechef.data.remote

import com.hmmelton.bytechef.data.model.remote.RemoteRecipe

/**
 * Interface defining remote data source for recipes.
 */
interface RemoteRecipeSource {

    /**
     * Fetch recipes belonging to a specific user.
     *
     * @param uid ID of user for whom to fetch recipes
     *
     * @return List of recipes belonging to user
     */
    suspend fun fetchRecipesForUser(uid: String): List<RemoteRecipe>

    /**
     * Fetch a specific recipe.
     *
     * @param recipeId ID of recipe to fetch
     *
     * @return Recipe in format of remote data source, or null if query failed
     */
    suspend fun fetchRecipeById(recipeId: String): RemoteRecipe?

    /**
     * Create a new recipe.
     *
     * @param recipe Recipe to add to remote data source
     *
     * @return whether or not the recipe creation was successful
     */
    suspend fun createRecipe(recipe: RemoteRecipe): Boolean

    /**
     * Update an existing recipe.
     *
     * @param recipe recipe object with updated data
     *
     * @return whether or not the recipe update was successful
     */
    suspend fun updateRecipe(recipe: RemoteRecipe): Boolean

    /**
     * Delete a recipe.
     *
     * @param recipeId ID of recipe to be deleted
     *
     * @return whether or not the recipe deletion was successful
     */
    suspend fun deleteRecipe(recipeId: String): Boolean
}
