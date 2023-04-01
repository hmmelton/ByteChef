package com.hmmelton.bytechef.data.model.ui

import com.hmmelton.bytechef.data.model.local.LocalRecipe
import com.hmmelton.bytechef.data.model.local.RecipeDietaryRestriction
import com.hmmelton.bytechef.data.model.local.RecipeInfo
import com.hmmelton.bytechef.data.model.local.RecipeIngredient
import com.hmmelton.bytechef.data.model.local.RecipeInstruction
import com.hmmelton.bytechef.data.model.remote.RemoteIngredient
import com.hmmelton.bytechef.data.model.remote.RemoteInstruction
import com.hmmelton.bytechef.data.model.remote.RemoteRecipe

data class Recipe(
    val id: String,
    val name: String,
    val description: String,
    val servings: Int,
    val cuisine: String,
    val mealType: String,
    val createdBy: String,
    val imageUri: String,
    val cookTimeMin: Int,
    val ingredients: List<Ingredient>,
    val instructions: List<Instruction>,
    val dietaryRestrictions: List<String>
)

/**
 * Extension function to help with converting a UI recipe object to a database recipe object.
 *
 * IMPORTANT NOTE: because this function creates new instances of [RecipeIngredient],
 * [RecipeInstruction], and [RecipeDietaryRestriction], it is only appropriate for creating new
 * database recipe objects. Using this function to update an existing locally-stored recipe will
 * result in duplicate sets of auxiliary database entries, as there will be different primary key
 * values.
 */
fun Recipe.toLocalRecipe(): LocalRecipe {
    val recipeInfo = RecipeInfo(
        id = id,
        name = name,
        description = description,
        servings = servings,
        cuisine = cuisine,
        cookTimeMin = cookTimeMin,
        imageUri = imageUri,
        mealType = mealType,
        createdBy = createdBy
    )
    val localIngredients = ingredients.map { ingredient ->
        RecipeIngredient(
            recipeId = id,
            name = ingredient.name,
            quantity = ingredient.quantity,
            unit = ingredient.unit,
            orderNum = ingredient.orderNum
        )
    }
    val localInstructions = instructions.map { instruction ->
        RecipeInstruction(
            recipeId = id,
            description = instruction.description,
            stepNum = instruction.stepNum
        )
    }
    val localDietaryRestrictions = dietaryRestrictions.map { restriction ->
        RecipeDietaryRestriction(recipeId = id, name = restriction)
    }

    return LocalRecipe(recipeInfo, localIngredients, localInstructions, localDietaryRestrictions)
}

fun Recipe.toRemoteRecipe(): RemoteRecipe {
    val remoteIngredients = ingredients.map { ingredient ->
        RemoteIngredient(
            name = ingredient.name,
            quantity = ingredient.quantity,
            unit = ingredient.unit,
            orderNum = ingredient.orderNum
        )
    }

    val remoteInstructions = instructions.map { instruction ->
        RemoteInstruction(
            description = instruction.description,
            stepNum = instruction.stepNum
        )
    }

    return RemoteRecipe(
        id = id,
        name = name,
        description = description,
        servings = servings,
        cuisine = cuisine,
        mealType = mealType,
        createdBy = createdBy,
        imageUri = imageUri,
        cookTimeMin = cookTimeMin,
        ingredients = remoteIngredients,
        instructions = remoteInstructions,
        dietaryRestrictions = dietaryRestrictions
    )
}

