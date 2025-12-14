package org.oierxjn.jarvis.model

import kotlinx.serialization.Serializable

@Serializable
data class MessageData(
    val id: Int,
    val sender_name: String,
    val content: String,
    val msg_time: String,
)
@Serializable
data class QQMessage(
    val messages: List<MessageData>,
    val total: Int,
    val pages: Int,
    val current_page: Int,
) {
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        other as QQMessage
//
//        if (messages != other.messages) return false
//        if (total != other.total) return false
//        if (pages != other.pages) return false
//        if (current_page != other.current_page) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        var result = total
//        result = 31 * result + pages
//        result = 31 * result + current_page
//        result = 31 * result + messages.hashCode()
//        return result
//    }
}