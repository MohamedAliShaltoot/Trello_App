package com.example.trello.di


import com.example.trello.data.repository.TrelloRepositoryImpl
import com.example.trello.domain.repository.TrelloRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTrelloRepository(impl: TrelloRepositoryImpl): TrelloRepository
}
