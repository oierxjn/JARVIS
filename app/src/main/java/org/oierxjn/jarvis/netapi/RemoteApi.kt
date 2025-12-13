package org.oierxjn.jarvis.netapi

import android.util.Log
import okio.IOException
import org.json.JSONObject
import org.oierxjn.jarvis.model.DataModel
import org.oierxjn.jarvis.netapi.NetRequestApi.getRequest
import kotlinx.serialization.json.Json
import org.oierxjn.jarvis.model.AiConfig

object RemoteApi {
    val baseUrl: String get() {
        return "${DataModel.remoteUrl}:${DataModel.remotePort}"
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

    fun getRemoteSetting(){
        fun onSuccess(json: String){
            DataModel.settingData = Json.decodeFromString<AiConfig>(json)
        }
        fun onError(e: String){
            Log.e("RemoteApi", e)
        }
        getRequest(
            "$baseUrl/setting",
            { json -> onSuccess(json) },
            { e -> onError(e) }
        )
    }
}