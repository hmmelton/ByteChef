package com.hmmelton.bytechef.di

import com.google.firebase.auth.FirebaseAuth
import com.hmmelton.bytechef.BuildConfig
import com.hmmelton.bytechef.data.auth.AuthManager
import com.hmmelton.bytechef.data.auth.AuthManagerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency injection module for authentication-related classes.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindAuthManager(authManagerImpl: AuthManagerImpl): AuthManager

    companion object {

        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth {
            val auth = FirebaseAuth.getInstance()

            // If this is a debug build, use the Firebase emulator
            if (BuildConfig.DEBUG) {
                auth.useEmulator("10.0.2.2", 9099)
            }
            return auth
        }
    }
}
