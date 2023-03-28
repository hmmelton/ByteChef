package com.hmmelton.bytechef.data.model.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeInfo(
    @PrimaryKey val id: String,
    val name: String,
    @ColumnInfo(name = "image_url") val imageUrl: String,
    @ColumnInfo(name = "meal_type") val mealType: String,
    val cuisine: String,
    @ColumnInfo(name = "created_by") val createdBy: String
)
