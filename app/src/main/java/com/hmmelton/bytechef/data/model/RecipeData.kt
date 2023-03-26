package com.hmmelton.bytechef.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "recipes")
data class RecipeData(
    @PrimaryKey val id: String,
    val name: String,
    @ColumnInfo(name = "image_url") val imageUrl: String,
    @TypeConverters(StringSetConverter::class) val ingredients: Set<String>,
    @TypeConverters(StringSetConverter::class) val instructions: Set<String>,
    @ColumnInfo(name = "meal_type") val mealType: String,
    val cuisine: String
)
