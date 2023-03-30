package com.hmmelton.bytechef.data.model.remote

import androidx.annotation.Keep
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

@Keep
data class RemoteRecipe(
    @Exclude
    var id: String = "",
    val title: String = "",
    val description: String = "",
    val servings: Int = 0,

    @get:PropertyName("created_by")
    @set:PropertyName("created_by")
    var createdBy: String = "",

    @get:PropertyName("image_uri")
    @set:PropertyName("image_uri")
    var imageUri: String = "",

    @get:PropertyName("cook_time")
    @set:PropertyName("cook_time")
    var cookTime: Int = 0,

    var instructions: List<String> = emptyList(),

    @get:PropertyName("ingredients")
    @set:PropertyName("ingredients")
    var ingredients: List<String> = emptyList(),

    @get:PropertyName("dietary_restrictions")
    @set:PropertyName("dietary_restrictions")
    var dietaryRestrictions: List<String> = emptyList()
)