package com.hmmelton.bytechef.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.hmmelton.bytechef.data.model.local.LocalRecipe
import com.hmmelton.bytechef.data.model.local.RecipeDietaryRestriction
import com.hmmelton.bytechef.data.model.local.RecipeInfo
import com.hmmelton.bytechef.data.model.local.RecipeIngredient
import com.hmmelton.bytechef.data.model.local.RecipeInstruction
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    // Insert section - recipe must be broken into components before being inserted

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeInfo(recipe: RecipeInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredients(ingredients: List<RecipeIngredient>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstructions(instructions: List<RecipeInstruction>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDietaryRestrictions(crossRef: List<RecipeDietaryRestriction>)

    @Transaction
    suspend fun insertFullRecipe(recipe: LocalRecipe) {
        insertRecipeInfo(recipe.recipeInfo)
        insertIngredients(recipe.ingredients)
        insertInstructions(recipe.instructions)
        insertDietaryRestrictions(recipe.dietaryRestrictions)
    }

    // Query section

    @Transaction
    @Query("SELECT * FROM recipes")
    fun getAllRecipes(): Flow<List<LocalRecipe>>

    @Transaction
    @Query("SELECT * FROM recipes WHERE id IN (:recipeIds)")
    fun getFullRecipesById(recipeIds: List<String>): Flow<List<LocalRecipe>>

    @Transaction
    @Query("SELECT * FROM recipes WHERE last_updated_timestamp > :lastSyncTimestamp")
    suspend fun getRecipeUpdatesSinceLastSync(lastSyncTimestamp: Long): List<LocalRecipe>

    @Transaction
    @Query("""
        SELECT * FROM recipes
        WHERE (:cuisine IS NULL OR cuisine = :cuisine)
        AND (:dietaryRestriction IS NULL OR id IN (
            SELECT recipe_id FROM recipe_dietary_restrictions
            WHERE name = :dietaryRestriction
        ))
    """)
    fun getFilteredFullRecipes(
        cuisine: String? = null,
        dietaryRestriction: String? = null,
    ): Flow<List<LocalRecipe>>

    @Transaction
    @Query("DELETE FROM recipes WHERE id = :recipeId")
    suspend fun deleteRecipe(recipeId: String)
}
