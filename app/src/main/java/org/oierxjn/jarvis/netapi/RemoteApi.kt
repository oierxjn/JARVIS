package org.oierxjn.jarvis.netapi

import android.util.Log
import okio.IOException
import org.json.JSONObject
import org.oierxjn.jarvis.model.DataModel
import org.oierxjn.jarvis.netapi.NetRequestApi.getRequest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.oierxjn.jarvis.model.SettingData

object RemoteApi {
    val baseUrl: String get() {
        return "http://${DataModel.remoteHost}:${DataModel.remotePort}/api"
    }


    fun processJson(json: String): JSONObject?{
        return try {
            val jsonObject = JSONObject(json)
            return jsonObject
        } catch (e: IOException){
            Log.e("RemoteApi", e.message ?: "未知错误")
            null
        }
    }

    suspend fun getRemoteSetting(){
        val response = getRequest("$baseUrl/settings")
        DataModel.settingData = Json.decodeFromString<SettingData>(response)
        DataModel.settingData.isFetched = true
    }

    suspend fun updateRemoteSetting(){
        val json = Json{
            encodeDefaults = true
        }
        val response = NetRequestApi.postRequest(
            "$baseUrl/settings",
            json.encodeToString(DataModel.settingData),
            "application/json"
        )
        Log.d("RemoteApi", "[JARVIS] 更新设置完成：${response}")
    }
}