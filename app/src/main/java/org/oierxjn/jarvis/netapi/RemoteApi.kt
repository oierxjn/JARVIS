package org.oierxjn.jarvis.netapi

import android.util.Log
import kotlinx.serialization.Serializable
import okio.IOException
import org.json.JSONObject
import org.oierxjn.jarvis.model.DataModel
import org.oierxjn.jarvis.netapi.NetRequestApi.getRequest
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONException
import org.oierxjn.jarvis.model.ChatListModel
import org.oierxjn.jarvis.model.DashboardStats
import org.oierxjn.jarvis.model.QQMessage
import org.oierxjn.jarvis.model.SettingData
import org.oierxjn.jarvis.model.friendInfo

object RemoteApi {
    val baseUrl: String get() {
        return "http://${DataModel.remoteHost}:${DataModel.remotePort}/api"
    }


    fun processJson(json: String): JSONObject{
        val jsonObject: JSONObject
        try {
            jsonObject = JSONObject(json)
        } catch (e: JSONException){
            Log.e("RemoteApi", e.message ?: "未知错误")
            throw JSONException(e)
        }
        return jsonObject
    }

    /**
     * 获取仪表盘统计
     */
    suspend fun getDashBoardStat(){
        val url = "${baseUrl}/stats"
        val response = getRequest(url)
        DataModel.dashBoardStats = Json.decodeFromString<DashboardStats>(response)
    }

    /**
     * 获取可用的聊天列表
     */
    suspend fun getAvailableChats(){
        val url = "${baseUrl}/available-chats"
        val response = getRequest(url)
        val json = processJson(response)

        val friendList: JSONArray = json.getJSONArray("friends")
        val groupList: JSONArray = json.getJSONArray("groups")
        DataModel.availableFriendInfoList = Json.decodeFromString(friendList.toString())
        DataModel.availableGroupInfoList = Json.decodeFromString(groupList.toString())
        Log.d("RemoteApi", "[JARVIS] 获取可用聊天列表完成")
    }


    /**
     * 添加监控聊天
     */
    suspend fun addMonitoredChat(){
        // TODO
        val url = "${baseUrl}/monitored-chats"
        val response = NetRequestApi.postRequest(url, "{}", "application/json")
    }

    /**
     * 获取已监控聊天列表
     */
    suspend fun getMonitoredChatList(){
        val url = "${baseUrl}/monitored-chats"
        val response = getRequest(url)
        DataModel.chatListModel = Json.decodeFromString<ChatListModel>(response)
    }

    /**
     * 获取聊天消息
     */
    suspend fun getMessageList(chatId: Int){
        val url = "${baseUrl}/messages/${chatId}"
        val response = getRequest(url)
        DataModel.messagesList = Json.decodeFromString<QQMessage>(response)
        Log.d("RemoteApi", "[JARVIS] 获取消息列表完成：${Json.encodeToString(DataModel.messagesList)}")
    }

    /**
     * 获取设置
     */
    suspend fun getRemoteSetting(){
        val url = "${baseUrl}/settings"
        val response = getRequest(url)
        DataModel.settingData = Json.decodeFromString<SettingData>(response)
        DataModel.settingData.isFetched = true
    }

    /**
     * 更新设置
     */
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