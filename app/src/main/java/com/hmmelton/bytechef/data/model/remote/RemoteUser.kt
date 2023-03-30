package com.hmmelton.bytechef.data.model.remote

import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName

@Keep
data class RemoteUser(
    var uid: String = "",
    var email: String = "",

    @get:PropertyName("display_name")
    @set:PropertyName("display_name")
    var displayName: String = "",

    @get:PropertyName("dietary_restrictions")
    @set:PropertyName("dietary_restrictions")
    var dietaryRestrictions: List<String> = emptyList(),

    @get:PropertyName("favorite_cuisines")
    @set:PropertyName("favorite_cuisines")
    var favoriteCuisines: List<String> = emptyList(),

    @get:PropertyName("favorite_recipe_ids")
    @set:PropertyName("favorite_recipe_ids")
    var favoriteRecipeIds: List<String> = emptyList()
)
