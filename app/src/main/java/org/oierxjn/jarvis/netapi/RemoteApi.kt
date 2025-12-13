package org.oierxjn.jarvis.netapi

import android.util.Log
import okio.IOException
import org.json.JSONObject
import org.oierxjn.jarvis.model.DataModel
import org.oierxjn.jarvis.netapi.NetRequestApi.getRequest
import kotlinx.serialization.json.Json
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

    fun getRemoteSetting(
        onSuccessLater: (String) -> Unit = {},
        onErrorLater: (String) -> Unit = {},
    ){
        fun onSuccess(json: String){
            Log.d("RemoteApi", "[JARVIS] 从端口获取数据成功:\n${json}")
            DataModel.settingData = Json.decodeFromString<SettingData>(json)
            Log.d("RemoteApi", "[JARVIS] 转换完成")
            onSuccessLater(json)
        }
        fun onError(e: String){
            Log.e("RemoteApi", e)
            onErrorLater(e)
        }
        getRequest(
            "$baseUrl/settings",
            { json -> onSuccess(json) },
            { e -> onError(e) }
        )
    }
}