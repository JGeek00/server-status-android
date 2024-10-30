package com.jgeek00.ServerStatus.di

import android.content.Context
import com.jgeek00.ServerStatus.repository.ApiRepository
import com.jgeek00.ServerStatus.repository.ServerInstancesRepository
import com.jgeek00.ServerStatus.repository.StatusRepository
import com.jgeek00.ServerStatus.services.ApiClient
import com.jgeek00.ServerStatus.services.DataStoreService
import com.jgeek00.ServerStatus.services.DatabaseService
import com.jgeek00.ServerStatus.viewmodels.StatusViewModel
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
    fun provideServerInstancesRepository(databaseService: DatabaseService, dataStoreService: DataStoreService, apiRepository: ApiRepository, statusRepository: StatusRepository): ServerInstancesRepository {
        return ServerInstancesRepository(databaseService, dataStoreService, apiRepository, statusRepository)
    }

    @Provides
    @Singleton
    fun provideApiRepository(): ApiRepository {
        return ApiRepository()
    }

    @Provides
    @Singleton
    fun provideStatusRepository(apiRepository: ApiRepository, @ApplicationContext context: Context): StatusRepository {
        return StatusRepository(context, apiRepository)
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

@EntryPoint
@InstallIn(SingletonComponent::class)
interface StatusRepositoryEntryPoint {
    val statusRepository: StatusRepository
}