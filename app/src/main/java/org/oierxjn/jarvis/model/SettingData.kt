package org.oierxjn.jarvis.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
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
    @Transient
    var isFetched = false
    @Transient
    var needUpdate = false

}
