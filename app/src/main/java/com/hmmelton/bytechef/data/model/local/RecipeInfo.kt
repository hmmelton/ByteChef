package com.hmmelton.bytechef.data.model.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeInfo(
    @PrimaryKey val id: String = "",
    val name: String,
    val description: String,
    val servings: Int,
    val cuisine: String,
    @ColumnInfo(name = "cook_time") val cookTimeMin: Int,
    @ColumnInfo(name = "image_uri") val imageUri: String,
    @ColumnInfo(name = "meal_type") val mealType: String,
    @ColumnInfo(name = "created_by") val createdBy: String,
    @ColumnInfo(name = "last_updated_timestamp")
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)
