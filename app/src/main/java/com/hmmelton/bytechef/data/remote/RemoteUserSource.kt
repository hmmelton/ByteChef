package com.hmmelton.bytechef.data.remote

import com.hmmelton.bytechef.data.auth.AuthManager
import com.hmmelton.bytechef.data.model.remote.RemoteUser

/**
 * Interface defining remote data source for user info data. This info is basic account data
 * including uid, email, display name, and various preferences.
 */
interface RemoteUserSource {

    /**
     * Create data for a new user.
     *
     * @param authInfo basic identifying info about the account
     * @param displayName user display name
     * @param dietaryRestrictions user's dietary restrictions (e.g. gluten-free, vegan)
     * @param favoriteCuisines user's favorite cuisines
     *
     * @return the newly-created user data object, or null if the creation failed
     */
    suspend fun createUserData(
        authInfo: AuthManager.AuthInfo,
        displayName: String,
        dietaryRestrictions: List<String>,
        favoriteCuisines: List<String>
    ): RemoteUser?

    /**
     * Update data for an existing user.
     *
     * @param uid ID of the user to be updated
     * @param favoriteRecipesIds list of IDs for the user's favorite recipes
     * @param dietaryRestrictions user's dietary restrictions (e.g. gluten-free, vegan)
     * @param favoriteCuisines user's favorite cuisines
     *
     * @return whether or not the udpate was successful
     */
    suspend fun updateUserData(
        uid: String,
        favoriteRecipesIds: List<String>? = null,
        dietaryRestrictions: List<String>? = null,
        favoriteCuisines: List<String>? = null
    ): Boolean

    /**
     * Fetch user data for provided ID.
     *
     * @param uid ID for the user whose data should be fetched
     *
     * @return user data, or null if the query failed
     */
    suspend fun fetchUserData(uid: String): RemoteUser?

    /**
     * Delete user with provided ID.
     *
     * @param uid ID of the user to be deleted
     *
     * @return whether or not the user deletion was successful
     */
    suspend fun deleteUserData(uid: String): Boolean
}
