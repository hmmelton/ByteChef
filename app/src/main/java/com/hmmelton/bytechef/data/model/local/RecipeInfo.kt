package com.hmmelton.bytechef.data.model.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeInfo(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val servings: Int,
    val cuisine: String,
    @ColumnInfo(name = "cook_time") val cookTimeMin: Int,
    @ColumnInfo(name = "image_url") val imageUrl: String,
    @ColumnInfo(name = "meal_type") val mealType: String,
    @ColumnInfo(name = "author_uid") val authorUid: String
)
