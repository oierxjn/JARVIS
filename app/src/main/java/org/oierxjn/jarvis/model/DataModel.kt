package org.oierxjn.jarvis.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object DataModel {
    var remoteHost = "100.79.34.39"
    var remotePort = 5000

    var settingData = SettingData()
}

object AppDataStore{
    private const val DATA_STORE_NAME = "app_user_preferences"
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = DATA_STORE_NAME
    )
    val REMOTE_HOST_KEY = stringPreferencesKey("remote_host_key")
    val REMOTE_PORT_KEY = intPreferencesKey("remote_port_key")

    suspend fun saveString(context: Context, key: Preferences.Key<String>, value: String) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }
    suspend fun saveInt(context: Context, key: Preferences.Key<Int>, value: Int) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }
    fun getStringFlow(context: Context, key: Preferences.Key<String>, defaultValue: String = ""): Flow<String> {
        return context.dataStore.data
            .map { preferences ->
                preferences[key] ?: defaultValue
            }
    }
    fun getIntFlow(context: Context, key: Preferences.Key<Int>, defaultValue: Int = 0): Flow<Int> {
        return context.dataStore.data
            .map { preferences ->
                preferences[key] ?: defaultValue
            }
    }
}