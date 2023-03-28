package com.hmmelton.bytechef.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hmmelton.bytechef.data.model.local.RecipeDietaryRestriction
import com.hmmelton.bytechef.data.model.local.RecipeInfo
import com.hmmelton.bytechef.data.model.local.RecipeIngredient
import com.hmmelton.bytechef.data.model.local.RecipeInstruction

@Database(
    entities = [
        RecipeInfo::class,
        RecipeIngredient::class,
        RecipeInstruction::class,
        RecipeDietaryRestriction::class
   ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun recipeDao(): RecipeDao
}
