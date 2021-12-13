package com.polotika.todoapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.polotika.todoapp.utils.AppConstants
import kotlinx.coroutines.flow.map

private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(AppConstants.PREFERENCES_NAME)

class AppPreferencesHelper(private val context: Context) :AppPreferences {

    private val sortingKey = stringPreferencesKey(AppConstants.PREFERENCES_SORTING_KEY)
    private val appTourGuideKey = booleanPreferencesKey(AppConstants.PREFERENCES_YOUR_GUIDE_KEY)

    override suspend fun setSortState(value:String) {
        context.dataStore.edit { pref ->
            pref[sortingKey] = value
        }
    }

    override fun getSortState() = context.dataStore.data.map { prefs ->
            prefs[sortingKey]?: AppConstants.SORT_BY_DATE_KEY
    }

    override fun isAppTourGuide() = context.dataStore.data.map { prefs ->
        prefs[appTourGuideKey]?:true
    }

    override suspend fun setAppTourState(value: Boolean) {
        context.dataStore.edit { prefs->
            prefs[appTourGuideKey] = value
        }
    }
}