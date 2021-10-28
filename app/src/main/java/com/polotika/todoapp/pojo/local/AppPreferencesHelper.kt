package com.polotika.todoapp.pojo.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.polotika.todoapp.pojo.utils.AppConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(AppConstants.preferencesName)

class AppPreferencesHelper(private val context: Context) :AppPreferences {

    private val sortingKey = stringPreferencesKey(AppConstants.pSortingKey)

    override suspend fun setSortState(value:String) {
        context.dataStore.edit { pref ->
            pref[sortingKey] = value
        }
    }

    override fun getSortState() = context.dataStore.data.map { prefs ->
            prefs[sortingKey]?: AppConstants.sortByDate
        }


}