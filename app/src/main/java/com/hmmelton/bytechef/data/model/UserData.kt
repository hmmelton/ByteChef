package com.hmmelton.bytechef.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "users")
data class UserData(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "display_name") val displayName: String,
    val email: String,
    @ColumnInfo(name = "dietary_restrictions")
    @TypeConverters(StringSetConverter::class)
    val dietaryRestrictions: Set<String>,
    @ColumnInfo(name = "favorite_cuisines")
    @TypeConverters(StringSetConverter::class)
    val favoriteCuisines: Set<String>,
    @ColumnInfo(name = "favorites")
    @TypeConverters(StringSetConverter::class)
    val favorites: Set<String>
)
