package com.hmmelton.bytechef.data.remote

import com.hmmelton.bytechef.data.model.remote.RemoteUser
import kotlinx.coroutines.flow.Flow

interface RemoteUserSource {
    fun getCurrentUid(): Flow<String?>

    suspend fun registerUser(
        email: String,
        password: String,
        dietaryRestrictions: List<String>,
        favoriteCuisines: List<String>
    ): RemoteUser?

    suspend fun loginUser(email: String, password: String): RemoteUser?

    suspend fun updateUserData(
        uid: String,
        favoriteRecipes: List<String>? = null,
        dietaryRestrictions: List<String>? = null,
        favoriteCuisines: List<String>? = null
    ): Boolean

    suspend fun fetchUserData(uid: String): RemoteUser?
}