package com.example.flightsearch.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

enum class ThemePreference { SYSTEM, LIGHT, DARK }

interface PreferencesRepository {
    val themePreference: Flow<ThemePreference>
    suspend fun setThemePreference(themePreference: ThemePreference)
}

class UserPreferencesRepository(private val context: Context) : PreferencesRepository {
    private val themeKey = stringPreferencesKey("theme_preference")

    override val themePreference: Flow<ThemePreference> = context.dataStore.data.map { preferences ->
        when (preferences[themeKey]) {
            ThemePreference.LIGHT.name -> ThemePreference.LIGHT
            ThemePreference.DARK.name -> ThemePreference.DARK
            else -> ThemePreference.SYSTEM
        }
    }

    override suspend fun setThemePreference(themePreference: ThemePreference) {
        context.dataStore.edit { preferences ->
            preferences[themeKey] = themePreference.name
        }
    }
}
