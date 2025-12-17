package org.oierxjn.jarvis.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Chat (
    val id: Int,
    val chat_type: Int,
    val peer_id: String,
    val name: String,
    val enabled: Boolean,
    @SerialName("last_fetch_time")
    val lastFetchTime: String?
)

@Serializable
data class ChatListModel(
    val chats: List<Chat>
)

val ChatType = mapOf(1 to "私聊", 2 to "群聊")