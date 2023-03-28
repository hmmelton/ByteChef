package com.hmmelton.bytechef.data.model.ui

data class Recipe(
    val id: String,
    val name: String,
    val imageUrl: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val dietaryRestrictions: List<String>,
    val mealType: String,
    val cuisine: String
)
