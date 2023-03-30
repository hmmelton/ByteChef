package com.hmmelton.bytechef.data.repositories

import com.hmmelton.bytechef.data.model.ui.Recipe

interface RecipesRepository {
    suspend fun fetchRecipesForUser(uid: String): Result<List<Recipe>>

    suspend fun createRecipe(recipe: Recipe): Result<Unit>

    suspend fun updateRecipe(recipe: Recipe): Result<Unit>

    suspend fun deleteRecipe(recipeId: String): Result<Unit>
}
