package com.example.trello


import android.content.Context
import androidx.room.Room
import com.example.trello.data.local.dao.BoardDao
import com.example.trello.data.local.dao.CardDao
import com.example.trello.data.local.dao.CardListDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TrelloDatabase =
        Room.databaseBuilder(context, TrelloDatabase::class.java, "trello.db").build()

    @Provides
    fun provideBoardDao(db: TrelloDatabase): BoardDao = db.boardDao()

    @Provides
    fun provideCardListDao(db: TrelloDatabase): CardListDao = db.cardListDao()

    @Provides
    fun provideCardDao(db: TrelloDatabase): CardDao = db.cardDao()
}