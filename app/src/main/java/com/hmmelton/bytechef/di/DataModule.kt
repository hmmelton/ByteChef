package com.hmmelton.bytechef.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.room.Room
import androidx.work.WorkManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.hmmelton.bytechef.BuildConfig
import com.hmmelton.bytechef.data.local.AppDatabase
import com.hmmelton.bytechef.data.local.RecipeDao
import com.hmmelton.bytechef.data.local.userDataStore
import com.hmmelton.bytechef.data.model.local.User
import com.hmmelton.bytechef.data.remote.RemoteRecipeSource
import com.hmmelton.bytechef.data.remote.RemoteRecipeSourceImpl
import com.hmmelton.bytechef.data.remote.RemoteUserSource
import com.hmmelton.bytechef.data.remote.RemoteUserSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency injection module for data layer classes.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    // Section: Remote data sources

    @Singleton
    @Binds
    abstract fun bindRemoteRecipeSource(
        remoteRecipeSourceImpl: RemoteRecipeSourceImpl
    ): RemoteRecipeSource

    @Singleton
    @Binds
    abstract fun bindRemoteUserSource(remoteUserSourceImpl: RemoteUserSourceImpl): RemoteUserSource

    companion object {

        // Section: Local data sources

        @Singleton
        @Provides
        fun provideUserDataStore(@ApplicationContext context: Context): DataStore<User> {
            return context.userDataStore
        }

        @Singleton
        @Provides
        fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
            return Room.databaseBuilder(
                context = context,
                klass = AppDatabase::class.java,
                name = "bytechef-database"
            ).build()
        }

        @Singleton
        @Provides
        fun provideRecipeDao(appDatabase: AppDatabase): RecipeDao {
            return appDatabase.recipeDao()
        }

        // Section: Firestore

        @Singleton
        @Provides
        fun provideFirebaseFirestore(): FirebaseFirestore {
            val firestore = FirebaseFirestore.getInstance()

            // If this is a debug build, use the Firebase emulator
            if (BuildConfig.DEBUG) {
                firestore.useEmulator("10.0.2.2", 8080)
            }
            return firestore
        }

        @Singleton
        @Provides
        @ForRecipe
        fun provideRecipeFirestoreCollection(firestore: FirebaseFirestore): CollectionReference {
            return firestore.collection("recipes")
        }

        @Singleton
        @Provides
        @ForUser
        fun provideUserFirestoreCollection(firestore: FirebaseFirestore): CollectionReference {
            return firestore.collection("users")
        }

        // Section work manager

        @Provides
        @Singleton
        fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
            return WorkManager.getInstance(context)
        }
    }
}
