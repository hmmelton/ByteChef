package com.hmmelton.bytechef.data.model

data class Recipe(
    val id: String,
    val name: String,
    val imageUrl: String,
    val ingredients: Set<String>,
    val instructions: Set<String>,
    val mealType: String,
    val cuisine: String
)
