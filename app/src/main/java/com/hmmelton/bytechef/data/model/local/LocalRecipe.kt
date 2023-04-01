package com.hmmelton.bytechef.data.model.local

import androidx.room.Embedded
import androidx.room.Relation
import com.hmmelton.bytechef.data.model.ui.Ingredient
import com.hmmelton.bytechef.data.model.ui.Instruction
import com.hmmelton.bytechef.data.model.ui.Recipe

data class LocalRecipe(
    @Embedded val recipeInfo: RecipeInfo,
    @Relation(parentColumn = "id", entityColumn = "recipe_id")
    val ingredients: List<RecipeIngredient>,
    @Relation(parentColumn = "id", entityColumn = "recipe_id")
    val instructions: List<RecipeInstruction>,
    @Relation(parentColumn = "id", entityColumn = "recipe_id")
    val dietaryRestrictions: List<RecipeDietaryRestriction>
)

fun LocalRecipe.toRecipe(): Recipe {
    return Recipe(
        id = recipeInfo.id,
        name = recipeInfo.name,
        description = recipeInfo.description,
        servings = recipeInfo.servings,
        cuisine = recipeInfo.cuisine,
        mealType = recipeInfo.mealType,
        createdBy = recipeInfo.createdBy,
        imageUri = recipeInfo.imageUri,
        cookTimeMin = recipeInfo.cookTimeMin,
        ingredients = ingredients
            .sortedBy { it.orderNum }
            .map { Ingredient(it.name, it.quantity, it.unit, it.orderNum) },
        instructions = instructions
            .sortedBy { it.stepNum }
            .map { Instruction(it.description, it.stepNum) },
        dietaryRestrictions = dietaryRestrictions.map { it.name }
    )
}
