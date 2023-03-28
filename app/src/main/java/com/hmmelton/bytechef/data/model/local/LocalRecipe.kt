package com.hmmelton.bytechef.data.model.local

import androidx.room.Embedded
import androidx.room.Relation

data class LocalRecipe(
    @Embedded val recipe: RecipeInfo,
    @Relation(parentColumn = "id", entityColumn = "recipe_id")
    val ingredients: List<RecipeIngredient>,
    @Relation(parentColumn = "id", entityColumn = "recipe_id")
    val instructions: List<RecipeInstruction>,
    @Relation(parentColumn = "id", entityColumn = "recipe_id")
    val dietaryRestrictions: List<RecipeDietaryRestriction>
)
