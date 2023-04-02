package com.hmmelton.bytechef.data.repositories

import com.hmmelton.bytechef.data.auth.AuthManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Utility class for syncing repository data sources.
 */
class RepositorySyncManager(
    private val authManager: AuthManager,
    private val coroutineDispatcher: CoroutineDispatcher,
    private val syncableRepositories: Set<@JvmSuppressWildcards SynchronizedRepository>
) {

    private var authInfoFlowJob: Job? = null

    /**
     * Begin listening for changes to user authentication, which dictate whether or not the app
     * should by syncing data.
     */
    fun startListeningForAuthChanges() {
        authInfoFlowJob?.cancel()
        authInfoFlowJob = CoroutineScope(coroutineDispatcher).launch {
            authManager.observeAuthInfo().collect { authInfo ->
                if (authInfo != null) {
                    startSyncingRepositories()
                } else {
                    stopSyncingRepositories()
                }
            }
        }
    }

    /**
     * This function is called to stop listening for changes in user authentication.
     */
    fun stopListeningForAuthChanges() {
        authInfoFlowJob?.cancel()
    }

    private fun startSyncingRepositories() {
        syncableRepositories.forEach { repository ->
            repository.startSync()
        }
    }

    private fun stopSyncingRepositories() {
        syncableRepositories.forEach { repository ->
            repository.stopSync()
        }
    }
}
