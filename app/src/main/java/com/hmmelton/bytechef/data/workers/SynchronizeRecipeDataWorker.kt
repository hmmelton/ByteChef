package com.hmmelton.bytechef.data.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hmmelton.bytechef.data.repositories.RecipeRepository
import com.hmmelton.bytechef.data.repositories.SynchronizedRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope

private const val TAG = "SynchronizeUserDataWorker"

/**
 * Worker class for synchronizing [RecipeRepository] data sources.
 */
@HiltWorker
class SynchronizeRecipeDataWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workParams: WorkerParameters,
    private val recipeRepository: RecipeRepository
) : CoroutineWorker(appContext, workParams) {

    override suspend fun doWork() = coroutineScope {
        try {
            val uid = inputData.getString(WorkKeys.UID) ?: throw MissingKeyException(WorkKeys.UID)
            // Attempt to force refresh/sync recipe data
            // TODO: make more robust sync that doesn't always give priority to remote data source
            (recipeRepository as? SynchronizedRepository)?.syncData(uid)
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to complete work", e)

            // If the exception was raised due to a missing input key, do not retry
            if (e is MissingKeyException) {
                Result.failure()
            } else {
                Result.retry()
            }
        }
    }
}
