package org.oierxjn.jarvis.model

import org.json.JSONObject


data object SettingData{
    var ai_endpoint = "https://api.deepseek.com/v1/chat/completions"
    var ai_api_key = "***"
    var ai_model = "deepseek-chat"
    var fetch_days = 1
    var napcat_host = "localhost"
    var napcat_port = 40653

    fun setSetting(jsonObject: JSONObject){
        ai_endpoint = jsonObject.getString("ai_endpoint")
        ai_api_key = jsonObject.getString("ai_api_key")
        ai_model = jsonObject.getString("ai_model")
        fetch_days = jsonObject.getInt("fetch_days")
        napcat_host = jsonObject.getString("napcat_host")
        napcat_port = jsonObject.getInt("napcat_port")
    }
    fun getSetting(): JSONObject{
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
