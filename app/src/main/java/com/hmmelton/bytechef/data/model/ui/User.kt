package com.hmmelton.bytechef.data.model.ui

data class User(
    val id: String,
    val displayName: String,
    val email: String,
    val dietaryRestrictions: List<String>,
    val favoriteCuisines: List<String>,
    val favorites: List<String>
)
