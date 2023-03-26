package com.hmmelton.bytechef.data.model

data class User(
    val id: String,
    val displayName: String,
    val email: String,
    val dietaryRestrictions: Set<String>,
    val favoriteCuisines: Set<String>,
    val favorites: Set<String>
)
