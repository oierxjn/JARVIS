package org.oierxjn.jarvis.model

import kotlinx.serialization.Serializable
import org.json.JSONObject


@Serializable
data class SettingData(
    var ai_endpoint: String = "https://api.deepseek.com/v1/chat/completions",
    var ai_api_key: String = "***",
    var ai_model: String = "deepseek-chat",
    var fetch_days: Int = 1,
    var napcat_host: String = "localhost",
    var napcat_port: Int = 40653,
){

    fun setData(jsonObject: JSONObject){
        ai_endpoint = jsonObject.getString("ai_endpoint")
        ai_api_key = jsonObject.getString("ai_api_key")
        ai_model = jsonObject.getString("ai_model")
        fetch_days = jsonObject.getInt("fetch_days")
        napcat_host = jsonObject.getString("napcat_host")
        napcat_port = jsonObject.getInt("napcat_port")
    }
    fun getData(): JSONObject{
        val jsonObject = JSONObject().apply {
            put("ai_endpoint", ai_endpoint)
            put("ai_api_key", ai_api_key)
            put("ai_model", ai_model)
            put("fetch_days", fetch_days)
            put("napcat_host", napcat_host)
            put("napcat_port", napcat_port)
        }
        return jsonObject
    }
}
