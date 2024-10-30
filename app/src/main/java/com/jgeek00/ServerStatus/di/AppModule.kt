package com.jgeek00.ServerStatus.di

import android.content.Context
import com.jgeek00.ServerStatus.repository.ServerInstancesRepository
import com.jgeek00.ServerStatus.services.DataStoreService
import com.jgeek00.ServerStatus.services.DatabaseService
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabaseService(@ApplicationContext context: Context): DatabaseService {
        return DatabaseService(context)
    }

    @Provides
    @Singleton
    fun provideDataStoreService(@ApplicationContext context: Context): DataStoreService {
        return DataStoreService(context)
    }

    @Provides
    @Singleton
    fun provideServerInstancesRepository(databaseService: DatabaseService, dataStoreService: DataStoreService): ServerInstancesRepository {
        return  ServerInstancesRepository(databaseService, dataStoreService)
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface DataStoreServiceEntryPoint {
    val dataStoreService: DataStoreService
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ServerInstancesRepositoryEntryPoint {
    val serverInstancesRepository: ServerInstancesRepository
}