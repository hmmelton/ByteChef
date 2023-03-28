package com.hmmelton.bytechef.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hmmelton.bytechef.data.model.local.RecipeInfo
import com.hmmelton.bytechef.data.model.local.LocalUser
import com.hmmelton.bytechef.data.model.StringSetConverter

@Database(
    entities = [RecipeInfo::class, LocalUser::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringSetConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun recipeDao(): RecipeDao
    abstract fun userDao(): UserDao
}
