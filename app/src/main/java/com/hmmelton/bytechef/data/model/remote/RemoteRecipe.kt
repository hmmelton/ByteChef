package com.hmmelton.bytechef.data.model.remote

import androidx.annotation.Keep
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import com.hmmelton.bytechef.data.model.local.LocalRecipe
import com.hmmelton.bytechef.data.model.local.RecipeDietaryRestriction
import com.hmmelton.bytechef.data.model.local.RecipeInfo
import com.hmmelton.bytechef.data.model.local.RecipeIngredient
import com.hmmelton.bytechef.data.model.local.RecipeInstruction

@Keep
data class RemoteRecipe(
    @Exclude
    var id: String = "",
    val name: String = "",
    val description: String = "",
    val servings: Int = 0,
    val cuisine: String = "",

    @get:PropertyName("meal_type")
    @set:PropertyName("meal_type")
    var mealType: String = "",

    @get:PropertyName("created_by")
    @set:PropertyName("created_by")
    var createdBy: String = "",

    @get:PropertyName("image_uri")
    @set:PropertyName("image_uri")
    var imageUri: String = "",

    @get:PropertyName("cook_time_min")
    @set:PropertyName("cook_time_min")
    var cookTimeMin: Int = 0,

    @get:PropertyName("last_updated_timestamp")
    @set:PropertyName("last_updated_timestamp")
    var lastUpdatedTimstamp: Long = 0,

    var instructions: List<RemoteInstruction> = emptyList(),
    var ingredients: List<RemoteIngredient> = emptyList(),

    @get:PropertyName("dietary_restrictions")
    @set:PropertyName("dietary_restrictions")
    var dietaryRestrictions: List<String> = emptyList()
)

fun RemoteRecipe.toLocalRecipe(): LocalRecipe {
    val recipeInfo = RecipeInfo(
        id = id,
        name = name,
        description = description,
        servings = servings,
        cuisine = cuisine,
        mealType = mealType,
        createdBy = createdBy,
        imageUri = imageUri,
        cookTimeMin = cookTimeMin,
        lastUpdatedTimestamp = lastUpdatedTimstamp
    )

    val localIngredients = ingredients.map { remoteIngredient ->
        RecipeIngredient(
            recipeId = id,
            name = remoteIngredient.name,
            quantity = remoteIngredient.quantity,
            unit = remoteIngredient.unit,
            orderNum = remoteIngredient.orderNum
        )
    }

    val localInstructions = instructions.map { remoteInstruction ->
        RecipeInstruction(
            recipeId = id,
            description = remoteInstruction.description,
            stepNum = remoteInstruction.stepNum
        )
    }

    val localDietaryRestrictions = dietaryRestrictions.map { restriction ->
        RecipeDietaryRestriction(
            recipeId = id,
            name = restriction
        )
    }

    return LocalRecipe(
        recipeInfo = recipeInfo,
        ingredients = localIngredients,
        instructions = localInstructions,
        dietaryRestrictions = localDietaryRestrictions
    )
}

