package com.polotika.todoapp.pojo.local

import kotlinx.coroutines.flow.Flow

interface AppPreferences {

    suspend fun setSortState(value:String)

    fun getSortState() : Flow<String>
}