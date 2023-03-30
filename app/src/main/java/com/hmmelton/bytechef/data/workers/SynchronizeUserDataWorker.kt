package com.hmmelton.bytechef.data.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hmmelton.bytechef.data.repositories.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope

private const val TAG = "SynchronizeUserDataWorker"

@HiltWorker
class SynchronizeUserDataWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workParams: WorkerParameters,
    private val userRepository: UserRepository
) : CoroutineWorker(appContext, workParams) {

    override suspend fun doWork(): Result = coroutineScope {
        try {
            // Attempt to force refresh/sync user data
            // TODO: make more robust sync that doesn't always give priority to remote data source
            userRepository.forceRefreshUser()
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to complete work", e)
            // If the sync fails, retry
            Result.retry()
        }
    }
}