package com.hmmelton.bytechef.data.remote

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.hmmelton.bytechef.data.model.remote.RemoteRecipe
import kotlinx.coroutines.tasks.await

private const val TAG = "RemoteRecipeDataSource"

/**
 * This class is used to interact with the remote Firebase Firestore data source to manage
 * recipe data. The remote recipe data class used is [RemoteRecipe].
 */
class RemoteRecipeDataSource(private val reference: CollectionReference) {

    /**
     * Fetch all recipes created by the user with the provided UID.
     *
     * @param uid The user ID for which to fetch recipes.
     */
    suspend fun fetchRecipesForUser(uid: String): List<RemoteRecipe> {
        return try {
            val querySnapshot = reference.whereEqualTo("created_by", uid).get().await()
            querySnapshot.documents.mapNotNull {  document ->
                val recipe = document.toObject(RemoteRecipe::class.java)
                recipe?.apply { id = document.id }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch recipes for user", e)
            emptyList()
        }
    }

    /**
     * Fetch a single recipe by its ID.
     *
     * @param recipeId ID of the recipe to fetch.
     */
    suspend fun fetchRecipeById(recipeId: String): RemoteRecipe? {
        return try {
            val documentSnapshot = reference.document(recipeId).get().await()
            val recipe = documentSnapshot.toObject(RemoteRecipe::class.java)
            recipe?.apply { id = documentSnapshot.id }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch recipe by ID", e)
            null
        }
    }

    /**
     * Add a new recipe to the remote Firestore collection.
     *
     * @param recipe The recipe to add.
     */
    suspend fun addRecipe(recipe: RemoteRecipe): RemoteRecipe? {
        return try {
            val documentReference = reference.add(recipe).await()
            val documentSnapshot = documentReference.get().await()
            val newRecipe = documentSnapshot.toObject(RemoteRecipe::class.java)
            newRecipe?.apply { id = documentSnapshot.id }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add recipe", e)
            null
        }
    }

    /**
     * Update an existing recipe in the remote Firestore collection.
     *
     * @param recipe The updated recipe to save.
     */
    suspend fun updateRecipe(recipe: RemoteRecipe): Boolean {
        return try {
            reference.document(recipe.id).set(recipe).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update recipe", e)
            false
        }
    }

    /**
     * Delete a recipe by its ID.
     *
     * @param recipeId ID of the recipe to delete.
     */
    suspend fun deleteRecipe(recipeId: String): Boolean {
        return try {
            reference.document(recipeId).delete().await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete recipe", e)
            false
        }
    }
}
