package com.hmmelton.bytechef.data.model.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recipe_dietary_restrictions",
    foreignKeys = [
        ForeignKey(
            entity = RecipeInfo::class,
            parentColumns = ["id"],
            childColumns = ["recipe_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("recipe_id")]
)
data class RecipeDietaryRestriction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "recipe_id") val recipeId: String,
    val name: String
)

