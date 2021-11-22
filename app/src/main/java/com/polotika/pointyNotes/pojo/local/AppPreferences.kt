package com.polotika.pointyNotes.pojo.local

import kotlinx.coroutines.flow.Flow

interface AppPreferences {

    suspend fun setSortState(value:String)

    fun getSortState() : Flow<String>

    fun isAppTourGuide():Flow<Boolean>

    suspend fun setAppTourState(value:Boolean)
}