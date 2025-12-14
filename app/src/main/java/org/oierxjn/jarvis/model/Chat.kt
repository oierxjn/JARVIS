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
    val lastFetchTime: String
)

@Serializable
data class ChatList(
    val chats: List<Chat>
)