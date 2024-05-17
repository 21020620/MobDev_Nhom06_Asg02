package com.example.mobdev2.ui.screens.book

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import java.io.IOException


private const val USER_PREFERENCES_NAME = "user_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCES_NAME )
@Single
class UserPreferences(
    context: Context
) {
    // Create a DataStore instance using the preferencesDataStore delegate, with the Context as
// receiver.
    private val dataStore: DataStore<Preferences> = context.dataStore


    private val BACKGROUND = stringPreferencesKey("bg");
    private val FONT = stringPreferencesKey("font");
    private val FONT_SIZE = floatPreferencesKey("font_size");
    private val TEXT_COLOR = stringPreferencesKey("text_color");
    private val THEME = booleanPreferencesKey("theme");

    suspend fun saveTheme(theme: Boolean) {
        dataStore.edit { preferences ->
            preferences[THEME] = theme
        }
    }
    suspend fun saveBackground(bg: String) {
        dataStore.edit { preferences ->
            preferences[BACKGROUND] = bg
        }
    }

    suspend fun saveFont(string: String) {
        dataStore.edit { preferences ->
            preferences[FONT] = string
        }
    }

    suspend fun saveFontSize(float: Float) {
        dataStore.edit { preferences ->
            preferences[FONT_SIZE] = float
        }
    }

    suspend fun saveTextColor(string: String) {
        dataStore.edit { preferences ->
            preferences[TEXT_COLOR] = string
        }
    }

    val settings: Flow<Any> = dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
        preferences[BACKGROUND] ?: "current"
    }
    val backgroundFlow: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                exception.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[BACKGROUND] ?: "current"
        }

    val themeFlow: Flow<Boolean> = dataStore.data.
    catch { exception ->
        if (exception is IOException) {
            exception.printStackTrace()
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }
        .map { preferences ->
            preferences[THEME] ?: false
        }
    val textColorFlow: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                exception.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[TEXT_COLOR] ?: "current"
        }
    val fontFlow: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                exception.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[FONT] ?: "System Default"
        }
    val fontSizeFlow: Flow<Float> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                exception.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[FONT_SIZE] ?: 14f
        }


}