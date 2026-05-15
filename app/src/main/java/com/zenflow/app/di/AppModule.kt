package com.zenflow.app.di

import android.content.Context
import com.zenflow.app.data.local.ZenFlowDatabase
import com.zenflow.app.data.repository.ZenFlowRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ZenFlowDatabase {
        return ZenFlowDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideRepository(database: ZenFlowDatabase): ZenFlowRepository {
        return ZenFlowRepository(
            sectionDao = database.sectionDao(),
            taskDao = database.taskDao(),
            dailyStatsDao = database.dailyStatsDao(),
            goalDao = database.goalDao(),
            focusSessionDao = database.focusSessionDao()
        )
    }
}