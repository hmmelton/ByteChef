package com.hmmelton.bytechef.data.model.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "instructions",
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
data class RecipeInstruction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "recipe_id") val recipeId: String,
    val description: String,
    @ColumnInfo(name = "step_num") val stepNum: Int
)

