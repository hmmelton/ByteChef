package com.hmmelton.bytechef.data.repositories

import User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun isAuthenticated(): Flow<Boolean>

    suspend fun registerUser(
        email: String,
        password: String,
        dietaryRestrictions: List<String>,
        favoriteCuisines: List<String>
    ): User?

    suspend fun loginUser(email: String, password: String): User?

    suspend fun updateUserData(
        uid: String,
        favoriteRecipeIds: List<String>? = null,
        dietaryRestrictions: List<String>? = null,
        favoriteCuisines: List<String>? = null
    ): Boolean

    suspend fun getUser(): Flow<User?>

    suspend fun forceRefreshUser(): Boolean
}
