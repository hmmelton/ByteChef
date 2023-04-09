package com.hmmelton.bytechef.data.repositories

/**
 * Interface for data repositories that need to sync between multiple data sources.
 */
interface SynchronizedRepository {
    suspend fun syncData(uid: String): Boolean
    suspend fun startSync()
    suspend fun stopSync()
}
