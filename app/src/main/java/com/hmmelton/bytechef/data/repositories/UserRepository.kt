package com.hmmelton.bytechef.data.repositories

import User
import com.hmmelton.bytechef.data.auth.AuthManager
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining a repository for interacting with user personal data.
 */
interface UserRepository {

    /**
     * Create new user data object. This function is used after new user registration.
     *
     * @param authInfo basic identifying info for user
     * @param displayName user's display name
     * @param dietaryRestrictions list of user's dietary restrictions (e.g. gluten-free, vegan)
     * @param favoriteCuisines list of user's favorite cuisines
     *
     * @return whether or not user data creation was successful
     */
    suspend fun createUserData(
        authInfo: AuthManager.AuthInfo,
        displayName: String,
        dietaryRestrictions: List<String>,
        favoriteCuisines: List<String>
    ): Boolean

    /**
     * Update user's favorites recipes, dietary restrictions, and favorite cuisines.
     *
     * @param uid user's UID.
     * @param favoriteRecipeIds list of user's favorite recipes.
     * @param dietaryRestrictions list of user's dietary restrictions.
     * @param favoriteCuisines list of user's favorite cuisines.
     *
     * @return whether or not update was successful
     */
    suspend fun updateUserData(
        uid: String,
        favoriteRecipeIds: List<String>? = null,
        dietaryRestrictions: List<String>? = null,
        favoriteCuisines: List<String>? = null
    ): Boolean

    /**
     * Delete info data for user with given uid.
     *
     * @param uid id of the user to be deleted
     * @param localSourceOnly indicates if only the local data source should be cleared. This may
     *                        be called in the case of a user logout, where the remote data should
     *                        be kept for future logins
     *
     * @return whether or not the deletion was successful
     */
    suspend fun deleteUserData(uid: String, localSourceOnly: Boolean): Boolean

    /**
     * Flow for reading user data.
     */
    fun observeUser(): Flow<User?>

    /**
     * Force a refresh from the remote data source.
     *
     * @param uid ID of current user
     *
     * @return whether or not the refresh was successful
     */
    suspend fun forceRefreshUser(uid: String): Boolean
}
