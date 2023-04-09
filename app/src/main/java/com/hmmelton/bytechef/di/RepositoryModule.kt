package com.hmmelton.bytechef.di

import com.hmmelton.bytechef.data.repositories.RecipeRepository
import com.hmmelton.bytechef.data.repositories.RecipeRepositoryImpl
import com.hmmelton.bytechef.data.repositories.SynchronizedRepository
import com.hmmelton.bytechef.data.repositories.UserRepository
import com.hmmelton.bytechef.data.repositories.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

/**
 * Dependency injection module for repository classes.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    @IntoSet
    abstract fun bindRecipeRepoAsSynchronizedRepo(
        recipeRepositoryImpl: RecipeRepositoryImpl
    ): SynchronizedRepository

    @Singleton
    @Binds
    @IntoSet
    abstract fun bindUserRepoAsSynchronizedRepo(
        userRepositoryImpl: UserRepositoryImpl
    ): SynchronizedRepository

    @Singleton
    @Binds
    abstract fun bindRecipeRepository(recipeRepositoryImpl: RecipeRepositoryImpl): RecipeRepository

    @Singleton
    @Binds
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    companion object {

        @Singleton
        @Provides
        fun provideCoroutineDispatcher(): CoroutineDispatcher {
            return Dispatchers.IO
        }
    }
}