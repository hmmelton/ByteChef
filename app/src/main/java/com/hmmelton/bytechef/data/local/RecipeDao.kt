package com.hmmelton.bytechef.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.hmmelton.bytechef.data.model.local.LocalRecipe
import com.hmmelton.bytechef.data.model.local.RecipeDietaryRestriction
import com.hmmelton.bytechef.data.model.local.RecipeIngredient
import com.hmmelton.bytechef.data.model.local.RecipeInstruction
import com.hmmelton.bytechef.data.model.local.RecipeInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    // Insert section - recipe must be broken into components before being inserted

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredients(ingredients: List<RecipeIngredient>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstructions(instructions: List<RecipeInstruction>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDietaryRestrictions(crossRef: List<RecipeDietaryRestriction>)

    @Transaction
    suspend fun insertFullRecipe(fullRecipe: LocalRecipe) {
        insertRecipe(fullRecipe.recipe)
        insertIngredients(fullRecipe.ingredients)
        insertInstructions(fullRecipe.instructions)
        insertDietaryRestrictions(fullRecipe.dietaryRestrictions)
    }

    // Query section

    @Transaction
    @Query("SELECT * FROM recipes WHERE id IN (:recipeIds)")
    suspend fun getFullRecipesById(recipeIds: List<String>): Flow<LocalRecipe>

    @Transaction
    @Query("""
        SELECT * FROM recipes
        WHERE (:cuisine IS NULL OR cuisine = :cuisine)
        AND (:dietaryRestriction IS NULL OR id IN (
            SELECT recipe_id FROM recipe_dietary_restrictions
            WHERE name = :dietaryRestriction
        ))
    """)
    suspend fun getFilteredFullRecipes(
        cuisine: String? = null,
        dietaryRestriction: String? = null,
    ): Flow<LocalRecipe>
}
