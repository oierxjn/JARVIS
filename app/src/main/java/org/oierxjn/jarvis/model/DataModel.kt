package org.oierxjn.jarvis.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

object DataModel {
    var remoteHost = "100.79.34.39"
    var remotePort = 5000

    var settingData = SettingData()

    lateinit var messagesList: QQMessage
    lateinit var chatListModel: ChatListModel
    lateinit var dashBoardStats: DashboardStats

    val dashBoardViewModel = DashboardViewModel()
    suspend fun getLocalSetting(context: Context){
        coroutineScope {
            val remoteHostTask = async {
                AppDataStore.getStringFlow(context, AppDataStore.REMOTE_HOST_KEY, remoteHost).first()
            }
            val remotePortTask = async {
                AppDataStore.getIntFlow(context, AppDataStore.REMOTE_PORT_KEY, remotePort).first()
            }
            remoteHost = remoteHostTask.await()
            remotePort = remotePortTask.await()
        }
    }

    suspend fun saveLocalSetting(context: Context){
        coroutineScope {
            val remoteHostTask = launch {
                AppDataStore.saveString(context, AppDataStore.REMOTE_HOST_KEY, remoteHost)
            }
            val remotePortTask = launch {
                AppDataStore.saveInt(context, AppDataStore.REMOTE_PORT_KEY, remotePort)
            }
            listOf(remotePortTask, remoteHostTask).joinAll()
        }
    }
}

object LoadedFlag{
    var isHomeLoaded = false
    var isChatsLoaded = false
    var isSettingsLoaded = false
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