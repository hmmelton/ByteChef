package com.hmmelton.bytechef.data.local

import User
import android.content.Context
import kotlinx.coroutines.flow.Flow

class UserStore(context: Context) {
    private val dataStore = context.userDataStore
    val user: Flow<User> = dataStore.data

    suspend fun updateUser(newUser: User) {
        dataStore.updateData { currentUser ->
            newUser
        }
    }

    suspend fun clearUser() {
        dataStore.updateData { currentUser ->
            User.getDefaultInstance()
        }
    }
}
