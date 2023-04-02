package com.hmmelton.bytechef.data.auth

import kotlinx.coroutines.flow.Flow

/**
 * Interface used to define management of user authentication.
 */
interface AuthManager {

    /**
     * Function for observing basic user info. Null if user is not authenticated.
     */
    fun observeAuthInfo(): Flow<AuthInfo?>

    /**
     * Register a new user.
     *
     * @param email user email
     * @param password user password
     * @param dietaryRestrictions list of user's dietary restrictions (e.g. gluten-free, vegan)
     * @param favoriteCuisines list of user's favorite cuisines
     *
     * @return basic identifying info for user, null if registration failed
     */
    suspend fun registerUser(
        email: String,
        password: String,
        dietaryRestrictions: List<String>,
        favoriteCuisines: List<String>
    ): AuthInfo?

    /**
     * Login an existing user
     *
     * @param email user email
     * @param password user password
     *
     * @return basic identifying info for user, null if login failed
     */
    suspend fun loginUser(email: String, password: String): AuthInfo?

    /**
     * Data class to hold bare-bones info identifying user
     */
    data class AuthInfo(val uid: String, val email: String?)
}