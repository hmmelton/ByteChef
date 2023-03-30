package com.hmmelton.bytechef.data.remote

import com.hmmelton.bytechef.data.model.remote.RemoteRecipe

interface RemoteRecipeDataSource {
    suspend fun fetchRecipesForUser(uid: String): List<RemoteRecipe>

    suspend fun fetchRecipeById(recipeId: String): RemoteRecipe?

    suspend fun addRecipe(recipe: RemoteRecipe): RemoteRecipe?

    suspend fun updateRecipe(recipe: RemoteRecipe): Boolean

    suspend fun deleteRecipe(recipeId: String): Boolean
}