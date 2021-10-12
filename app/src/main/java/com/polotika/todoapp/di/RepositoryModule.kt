package com.polotika.todoapp.di

import android.content.Context
import com.polotika.todoapp.pojo.data.repository.NotesRepository
import com.polotika.todoapp.pojo.data.repository.NotesRepositoryImpl
import com.polotika.todoapp.pojo.local.NoteDatabase
import com.polotika.todoapp.pojo.local.NotesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRepositoryPattern(notesDao: NotesDao): NotesRepository{
        return NotesRepositoryImpl(notesDao)
    }

    @Provides
    @Singleton
    fun provideNotesDao(notesDatabase: NoteDatabase):NotesDao{
        return notesDatabase.notesDao()
    }

    @Provides
    @Singleton
    fun providesNotesDataBase(@ApplicationContext context:Context):NoteDatabase{
        return NoteDatabase.getInstance(context)
    }

}